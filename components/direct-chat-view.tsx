"use client";

import { useEffect, useRef } from "react";
import useSWR, { mutate } from "swr";
import { useAuth } from "@/lib/auth-context";
import {
  fetcher,
  createDirectMessage,
  deleteDirectMessage,
} from "@/lib/api";
import type { DirectMessage, Direct } from "@/lib/types";
import { MessageBubble } from "./message-bubble";
import { MessageInput } from "./message-input";
import { User as UserIcon } from "lucide-react";

interface DirectChatViewProps {
  directId: number;
}

export function DirectChatView({ directId }: DirectChatViewProps) {
  const { user } = useAuth();
  const scrollRef = useRef<HTMLDivElement>(null);

  const { data: messages = [] } = useSWR<DirectMessage[]>(
    `/api/directs/${directId}/messages`,
    fetcher,
    { refreshInterval: 3000 }
  );

  const { data: directs = [] } = useSWR<Direct[]>(
    user ? `/api/directs/${user.username}` : null,
    fetcher
  );

  const direct = directs.find((d) => d.id === directId);
  const otherUser =
    direct && user
      ? direct.sender.username === user.username
        ? direct.recipient
        : direct.sender
      : null;

  useEffect(() => {
    if (scrollRef.current) {
      scrollRef.current.scrollTop = scrollRef.current.scrollHeight;
    }
  }, [messages]);

  const handleSend = async (content: string) => {
    if (!user) return;
    await createDirectMessage(directId, {
      content,
      user: { id: user.id },
    });
    mutate(`/api/directs/${directId}/messages`);
  };

  const handleDelete = async (id: number) => {
    await deleteDirectMessage(id);
    mutate(`/api/directs/${directId}/messages`);
  };

  const sortedMessages = [...messages].sort(
    (a, b) =>
      new Date(a.dateOfSend).getTime() - new Date(b.dateOfSend).getTime()
  );

  return (
    <div className="flex h-full flex-col">
      {/* Header */}
      <header className="flex items-center gap-3 border-b border-border bg-card px-6 py-4">
        <div className="flex h-10 w-10 items-center justify-center rounded-full bg-primary/20 text-primary">
          <UserIcon className="h-5 w-5" />
        </div>
        <div>
          <h2 className="text-base font-semibold text-foreground">
            {otherUser?.username || "Loading..."}
          </h2>
          <p className="text-xs text-muted-foreground">Direct Message</p>
        </div>
      </header>

      {/* Messages */}
      <div ref={scrollRef} className="flex-1 overflow-y-auto py-4">
        {sortedMessages.length === 0 && (
          <div className="flex flex-col items-center justify-center gap-3 py-20">
            <div className="flex h-16 w-16 items-center justify-center rounded-full bg-muted">
              <UserIcon className="h-8 w-8 text-muted-foreground" />
            </div>
            <p className="text-sm text-muted-foreground">
              No messages yet. Say hello!
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
            onDelete={msg.user.id === user?.id ? handleDelete : undefined}
          />
        ))}
      </div>

      {/* Input */}
      <MessageInput
        onSend={handleSend}
        placeholder={`Message ${otherUser?.username || "user"}...`}
      />
    </div>
  );
}
