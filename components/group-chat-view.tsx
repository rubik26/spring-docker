"use client";

import { useEffect, useRef } from "react";
import useSWR, { mutate } from "swr";
import { useAuth } from "@/lib/auth-context";
import {
  fetcher,
  createGroupMessage,
  editGroupMessage,
  deleteGroupMessage,
} from "@/lib/api";
import type { GroupMessage, Group } from "@/lib/types";
import { MessageBubble } from "./message-bubble";
import { MessageInput } from "./message-input";
import { Hash, Users, Info } from "lucide-react";

interface GroupChatViewProps {
  groupId: number;
}

export function GroupChatView({ groupId }: GroupChatViewProps) {
  const { user } = useAuth();
  const scrollRef = useRef<HTMLDivElement>(null);

  const { data: messages = [] } = useSWR<GroupMessage[]>(
    `/api/groups/${groupId}/messages`,
    fetcher,
    { refreshInterval: 3000 }
  );

  const { data: groups = [] } = useSWR<Group[]>("/api/groups", fetcher);

  const group = groups.find((g) => g.id === groupId);

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  const handleSend = async (content: string) => {
    if (!user) return;
    await createGroupMessage(groupId, {
      content,
      user: { id: user.id },
    });
    mutate(`/api/groups/${groupId}/messages`);
  };

  const handleEdit = async (id: number, content: string) => {
    await editGroupMessage(id, { content, edited: true });
    mutate(`/api/groups/${groupId}/messages`);
  };

  const handleDelete = async (id: number) => {
    await deleteGroupMessage(id);
    mutate(`/api/groups/${groupId}/messages`);
  };

  const sortedMessages = [...messages].sort(
    (a, b) => new Date(a.dateOfSend).getTime() - new Date(b.dateOfSend).getTime()
  );

  return (
    <div className="flex h-full flex-col">
      {/* Header */}
      <header className="flex items-center justify-between border-b border-border bg-card px-6 py-4">
        <div className="flex items-center gap-3">
          <div className="flex h-10 w-10 items-center justify-center rounded-full bg-accent/20 text-accent">
            <Hash className="h-5 w-5" />
          </div>
          <div>
            <h2 className="text-base font-semibold text-foreground">
              {group?.name || "Loading..."}
            </h2>
            {group?.description && (
              <p className="text-xs text-muted-foreground">
                {group.description}
              </p>
            )}
          </div>
        </div>
        <div className="flex items-center gap-2">
          <div className="flex items-center gap-1.5 rounded-lg bg-muted px-3 py-1.5 text-xs text-muted-foreground">
            <Users className="h-3.5 w-3.5" />
            {group?.participants?.length || 0}
          </div>
        </div>
      </header>

      {/* Messages */}
      <div ref={scrollRef} className="flex-1 overflow-y-auto py-4">
        {sortedMessages.length === 0 && (
          <div className="flex flex-col items-center justify-center gap-3 py-20">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-muted">
              <Hash className="h-8 w-8 text-muted-foreground" />
            </div>
            <p className="text-sm text-muted-foreground">
              No messages yet. Start the conversation!
            </p>
          </div>
        )}
        {sortedMessages.map((msg) => (
          <MessageBubble
            key={msg.id}
            id={msg.id}
            content={msg.content}
            username={msg.user.username}
            date={msg.dateOfSend}
            isEdited={msg.edited}
            isOwnMessage={msg.user.id === user?.id}
            onEdit={msg.user.id === user?.id ? handleEdit : undefined}
            onDelete={msg.user.id === user?.id ? handleDelete : undefined}
          />
        ))}
      </div>

      {/* Input */}
      <MessageInput
        onSend={handleSend}
        placeholder={`Message #${group?.name || "group"}...`}
      />
    </div>
  );
}
