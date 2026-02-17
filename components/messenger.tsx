"use client";

import { useState, useCallback, useEffect } from "react";
import useSWR, { mutate } from "swr";
import { useAuth } from "@/lib/auth-context";
import {
  directApi,
  directMessageApi,
  groupApi,
  groupMessageApi,
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
    },
    []
  );

  const handleCreateDirect = useCallback(
    async (userId: number) => {
      try {
        const direct = await directApi.createDirect(userId, {});
        await mutate("directs");
        setActiveChat({ type: "direct", id: direct.id });
      } catch (err) {
        console.error("Failed to create direct:", err);
      }
    },
    []
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
    async (content: string) => {
      if (!activeChat) return;
      try {
        if (activeChat.type === "direct") {
          await directMessageApi.createMessage(activeChat.id, { content });
          await mutate(`direct-${activeChat.id}-messages`);
        } else {
          await groupMessageApi.createMessage(activeChat.id, { content });
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

  const messages =
    activeChat?.type === "direct" ? directMessages : groupMessages;

  const chatName = activeChat
    ? activeChat.type === "direct"
      ? `Direct #${activeChat.id}`
      : groups.find((g) => g.id === activeChat.id)?.name || `Group #${activeChat.id}`
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
      />
      {activeChat ? (
        <ChatView
          type={activeChat.type}
          chatId={activeChat.id}
          chatName={chatName}
          messages={messages}
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
