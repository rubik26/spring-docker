"use client";

import { MessageCircle } from "lucide-react";

export function EmptyState() {
  return (
    <div className="flex flex-1 flex-col items-center justify-center bg-background">
      <div className="flex flex-col items-center gap-4">
        <div className="flex h-20 w-20 items-center justify-center rounded-2xl bg-secondary">
          <MessageCircle className="h-10 w-10 text-muted-foreground" />
        </div>
        <div className="flex flex-col items-center gap-1.5">
          <h2 className="text-lg font-semibold text-foreground">
            Welcome to Messenger
          </h2>
          <p className="max-w-xs text-center text-sm text-muted-foreground leading-relaxed">
            Select a conversation from the sidebar or start a new one to begin
            chatting.
          </p>
        </div>
      </div>
    </div>
  );
}
