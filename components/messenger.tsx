"use client";

import { useState, useCallback, useEffect, useRef } from "react";
import useSWR, { mutate } from "swr";
import { useAuth } from "@/lib/auth-context";
import {
  directApi,
  directMessageApi,
  directMessageImageApi,
  groupApi,
  groupMessageApi,
  groupMessageImageApi,
  userApi,
  type Direct,
  type Group,
  type User,
  type DirectMessage,
  type GroupMessage,
} from "@/lib/api";
import { Sidebar } from "@/components/sidebar";
import { ChatView } from "@/components/chat-view";
import { EmptyState } from "@/components/empty-state";

export function Messenger() {
  const { username } = useAuth();
  const [activeChat, setActiveChat] = useState<{
    type: "direct" | "group";
    id: number;
  } | null>(null);
  const [sidebarCollapsed, setSidebarCollapsed] = useState(false);

  // Maps directId -> other user's username
  const [directNameMap, setDirectNameMap] = useState<Record<number, string>>(
    {}
  );
  // Track who created each direct so we can resolve the other user
  const directCreationRef = useRef<Record<number, number>>({});

  // Fetch data
  const { data: users = [] } = useSWR<User[]>("users", () =>
    userApi.getUsers()
  );
  const { data: directs = [] } = useSWR<Direct[]>("directs", () =>
    directApi.getMyDirects()
  );
  const { data: groups = [] } = useSWR<Group[]>("groups", () =>
    groupApi.getGroups()
  );

  // Fetch messages for active chat
  const { data: directMessages = [] } = useSWR<DirectMessage[]>(
    activeChat?.type === "direct" ? `direct-${activeChat.id}-messages` : null,
    () => directMessageApi.getMessages(activeChat!.id)
  );

  const { data: groupMessages = [] } = useSWR<GroupMessage[]>(
    activeChat?.type === "group" ? `group-${activeChat.id}-messages` : null,
    () => groupMessageApi.getMessages(activeChat!.id)
  );

  // Resolve usernames for direct chats by fetching messages per user
  useEffect(() => {
    if (!username || directs.length === 0 || users.length === 0) return;

    const resolveNames = async () => {
      const newMap: Record<number, string> = { ...directNameMap };

      for (const direct of directs) {
        if (newMap[direct.id]) continue;

        // Check if we created this direct and know the recipient
        const recipientId = directCreationRef.current[direct.id];
        if (recipientId) {
          const recipientUser = users.find((u) => u.id === recipientId);
          if (recipientUser) {
            newMap[direct.id] = recipientUser.username;
            continue;
          }
        }

        // Try each user to find who else is in this conversation
        for (const user of users) {
          if (user.username === username) continue;
          try {
            const msgs = await directMessageApi.getMessagesByUsername(
              direct.id,
              user.username
            );
            if (msgs && msgs.length > 0) {
              newMap[direct.id] = user.username;
              break;
            }
          } catch {
            // user not in this direct, skip
          }
        }

        // Also try checking own messages endpoint to confirm this is our direct
        if (!newMap[direct.id]) {
          try {
            const myMsgs = await directMessageApi.getMessagesByUsername(
              direct.id,
              username
            );
            // We got access, so we're in this direct. Other user is unknown from here
            // Try all remaining users more aggressively
            if (myMsgs !== undefined) {
              for (const user of users) {
                if (user.username === username) continue;
                if (newMap[direct.id]) break;
                try {
                  await directMessageApi.getMessagesByUsername(
                    direct.id,
                    user.username
                  );
                  // If this doesn't throw, user has access - they might be the other party
                  newMap[direct.id] = user.username;
                } catch {
                  // not this user
                }
              }
            }
          } catch {
            // skip
          }
        }
      }

      setDirectNameMap(newMap);
    };

    resolveNames();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [directs, users, username]);

  // Figure out which messages belong to the current user in a direct
  // Since DirectMessage.user is @JsonIgnore, we fetch per-username to tag ownership
  const [ownMessageIds, setOwnMessageIds] = useState<Set<number>>(new Set());

  useEffect(() => {
    if (
      !activeChat ||
      activeChat.type !== "direct" ||
      !username
    )
      return;

    const fetchOwnership = async () => {
      try {
        const myMsgs = await directMessageApi.getMessagesByUsername(
          activeChat.id,
          username
        );
        setOwnMessageIds(new Set(myMsgs.map((m) => m.id)));
      } catch {
        setOwnMessageIds(new Set());
      }
    };

    fetchOwnership();
  }, [activeChat, username, directMessages]);

  // Polling for new messages
  useEffect(() => {
    if (!activeChat) return;
    const interval = setInterval(() => {
      if (activeChat.type === "direct") {
        mutate(`direct-${activeChat.id}-messages`);
      } else {
        mutate(`group-${activeChat.id}-messages`);
      }
    }, 3000);
    return () => clearInterval(interval);
  }, [activeChat]);

  const handleSelectChat = useCallback(
    (type: "direct" | "group", id: number) => {
      setActiveChat({ type, id });
      setOwnMessageIds(new Set());
    },
    []
  );

  const handleCreateDirect = useCallback(
    async (userId: number) => {
      try {
        const direct = await directApi.createDirect(userId, {});
        // Store the recipient so we can resolve the name
        directCreationRef.current[direct.id] = userId;
        const recipientUser = users.find((u) => u.id === userId);
        if (recipientUser) {
          setDirectNameMap((prev) => ({
            ...prev,
            [direct.id]: recipientUser.username,
          }));
        }
        await mutate("directs");
        setActiveChat({ type: "direct", id: direct.id });
      } catch (err) {
        console.error("Failed to create direct:", err);
      }
    },
    [users]
  );

  const handleCreateGroup = useCallback(
    async (name: string, description: string) => {
      try {
        const group = await groupApi.createGroup({ name, description });
        await mutate("groups");
        setActiveChat({ type: "group", id: group.id });
      } catch (err) {
        console.error("Failed to create group:", err);
      }
    },
    []
  );

  const handleSendMessage = useCallback(
    async (content: string, imageFile?: File) => {
      if (!activeChat) return;
      try {
        if (activeChat.type === "direct") {
          const msg = await directMessageApi.createMessage(activeChat.id, {
            content: content || " ",
          });
          if (imageFile) {
            await directMessageImageApi.uploadImage(msg.id, imageFile);
          }
          await mutate(`direct-${activeChat.id}-messages`);
        } else {
          const msg = await groupMessageApi.createMessage(activeChat.id, {
            content: content || " ",
          });
          if (imageFile) {
            await groupMessageImageApi.uploadImage(msg.id, imageFile);
          }
          await mutate(`group-${activeChat.id}-messages`);
        }
      } catch (err) {
        console.error("Failed to send message:", err);
      }
    },
    [activeChat]
  );

  const handleEditMessage = useCallback(
    async (id: number, content: string) => {
      if (!activeChat) return;
      try {
        if (activeChat.type === "direct") {
          await directMessageApi.editMessage(id, { content });
          await mutate(`direct-${activeChat.id}-messages`);
        } else {
          await groupMessageApi.editMessage(id, { content });
          await mutate(`group-${activeChat.id}-messages`);
        }
      } catch (err) {
        console.error("Failed to edit message:", err);
      }
    },
    [activeChat]
  );

  const handleDeleteMessage = useCallback(
    async (id: number) => {
      if (!activeChat) return;
      try {
        if (activeChat.type === "direct") {
          await directMessageApi.deleteMessage(id);
          await mutate(`direct-${activeChat.id}-messages`);
        } else {
          await groupMessageApi.deleteMessage(id);
          await mutate(`group-${activeChat.id}-messages`);
        }
      } catch (err) {
        console.error("Failed to delete message:", err);
      }
    },
    [activeChat]
  );

  // Enrich messages with ownership and images info
  const enrichedMessages = (() => {
    if (!activeChat) return [];

    if (activeChat.type === "direct") {
      return directMessages.map((msg) => ({
        ...msg,
        isOwn: ownMessageIds.has(msg.id),
        user: ownMessageIds.has(msg.id)
          ? { id: 0, username: username || "" }
          : {
              id: 0,
              username:
                directNameMap[activeChat.id] ||
                `User`,
            },
      }));
    }

    // Group messages already have user data
    return groupMessages;
  })();

  const chatName = activeChat
    ? activeChat.type === "direct"
      ? directNameMap[activeChat.id] || "Loading..."
      : groups.find((g) => g.id === activeChat.id)?.name ||
        `Group #${activeChat.id}`
    : "";

  return (
    <div className="flex h-screen">
      <Sidebar
        directs={directs}
        groups={groups}
        users={users}
        activeChat={activeChat}
        onSelectChat={handleSelectChat}
        onCreateDirect={handleCreateDirect}
        onCreateGroup={handleCreateGroup}
        currentUsername={username || ""}
        collapsed={sidebarCollapsed}
        onToggleCollapse={() => setSidebarCollapsed(!sidebarCollapsed)}
        directNameMap={directNameMap}
      />
      {activeChat ? (
        <ChatView
          type={activeChat.type}
          chatId={activeChat.id}
          chatName={chatName}
          messages={enrichedMessages}
          currentUsername={username || ""}
          onSendMessage={handleSendMessage}
          onEditMessage={handleEditMessage}
          onDeleteMessage={handleDeleteMessage}
          onToggleSidebar={() => setSidebarCollapsed(!sidebarCollapsed)}
          sidebarCollapsed={sidebarCollapsed}
        />
      ) : (
        <EmptyState />
      )}
    </div>
  );
}
