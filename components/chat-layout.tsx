"use client";

import { useState } from "react";
import { ChatSidebar } from "./chat-sidebar";
import { GroupChatView } from "./group-chat-view";
import { DirectChatView } from "./direct-chat-view";
import { MessageSquare } from "lucide-react";

export function ChatLayout() {
  const [activeChat, setActiveChat] = useState<{
    type: "direct" | "group";
    id: number;
  } | null>(null);

  return (
    <div className="flex h-screen overflow-hidden">
      <ChatSidebar activeChat={activeChat} onSelectChat={setActiveChat} />

      <main className="flex-1">
        {activeChat ? (
          activeChat.type === "group" ? (
            <GroupChatView key={activeChat.id} groupId={activeChat.id} />
          ) : (
            <DirectChatView key={activeChat.id} directId={activeChat.id} />
          )
        ) : (
          <div className="flex h-full flex-col items-center justify-center gap-4 bg-background">
            <div className="flex h-20 w-20 items-center justify-center rounded-2xl bg-card border border-border">
              <MessageSquare className="h-10 w-10 text-muted-foreground" />
            </div>
            <div className="text-center">
              <h2 className="text-lg font-semibold text-foreground">
                Welcome to Messenger
              </h2>
              <p className="mt-1 text-sm text-muted-foreground">
                Select a conversation from the sidebar to start chatting
              </p>
            </div>
          </div>
        )}
      </main>
    </div>
  );
}
