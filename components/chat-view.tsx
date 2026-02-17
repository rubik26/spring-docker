"use client";

import { useState, useRef, useEffect } from "react";
import { cn } from "@/lib/utils";
import {
  Send,
  Edit3,
  Trash2,
  X,
  Check,
  MessageCircle,
  Hash,
  Menu,
} from "lucide-react";

interface Message {
  id: number;
  content: string;
  dateOfSend: string;
  edited: boolean;
  watched?: boolean;
  user?: { id: number; username: string };
}

interface ChatViewProps {
  type: "direct" | "group";
  chatId: number;
  chatName: string;
  messages: Message[];
  currentUsername: string;
  onSendMessage: (content: string) => void;
  onEditMessage: (id: number, content: string) => void;
  onDeleteMessage: (id: number) => void;
  onToggleSidebar: () => void;
  sidebarCollapsed: boolean;
}

export function ChatView({
  type,
  chatId,
  chatName,
  messages,
  currentUsername,
  onSendMessage,
  onEditMessage,
  onDeleteMessage,
  onToggleSidebar,
  sidebarCollapsed,
}: ChatViewProps) {
  const [input, setInput] = useState("");
  const [editingId, setEditingId] = useState<number | null>(null);
  const [editContent, setEditContent] = useState("");
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    inputRef.current?.focus();
  }, [chatId]);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (input.trim()) {
      onSendMessage(input.trim());
      setInput("");
    }
  };

  const startEdit = (msg: Message) => {
    setEditingId(msg.id);
    setEditContent(msg.content);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditContent("");
  };

  const confirmEdit = () => {
    if (editingId !== null && editContent.trim()) {
      onEditMessage(editingId, editContent.trim());
      cancelEdit();
    }
  };

  const formatTime = (dateStr: string) => {
    if (!dateStr) return "";
    try {
      const date = new Date(dateStr);
      return date.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" });
    } catch {
      return "";
    }
  };

  const formatDate = (dateStr: string) => {
    if (!dateStr) return "";
    try {
      const date = new Date(dateStr);
      return date.toLocaleDateString([], {
        month: "short",
        day: "numeric",
        year: "numeric",
      });
    } catch {
      return "";
    }
  };

  // Group messages by date
  const groupedMessages: { date: string; messages: Message[] }[] = [];
  let lastDate = "";
  for (const msg of messages) {
    const date = formatDate(msg.dateOfSend);
    if (date !== lastDate) {
      groupedMessages.push({ date, messages: [msg] });
      lastDate = date;
    } else {
      groupedMessages[groupedMessages.length - 1].messages.push(msg);
    }
  }

  return (
    <div className="flex flex-1 flex-col bg-background">
      {/* Chat Header */}
      <header className="flex items-center gap-3 border-b border-border px-4 py-3">
        {sidebarCollapsed && (
          <button
            onClick={onToggleSidebar}
            className="flex h-8 w-8 items-center justify-center rounded-md text-muted-foreground transition-colors hover:text-foreground hover:bg-secondary md:hidden"
            aria-label="Open sidebar"
          >
            <Menu className="h-5 w-5" />
          </button>
        )}
        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-secondary text-sm font-medium text-secondary-foreground">
          {type === "direct" ? (
            <MessageCircle className="h-4 w-4" />
          ) : (
            <Hash className="h-4 w-4" />
          )}
        </div>
        <div className="flex flex-col">
          <span className="text-sm font-semibold text-foreground">
            {chatName}
          </span>
          <span className="text-xs text-muted-foreground">
            {type === "direct" ? "Direct message" : "Group chat"} &middot;{" "}
            {messages.length} messages
          </span>
        </div>
      </header>

      {/* Messages */}
      <div className="flex-1 overflow-y-auto px-4 py-4">
        {messages.length === 0 && (
          <div className="flex h-full flex-col items-center justify-center gap-3">
            <div className="flex h-16 w-16 items-center justify-center rounded-2xl bg-secondary">
              {type === "direct" ? (
                <MessageCircle className="h-8 w-8 text-muted-foreground" />
              ) : (
                <Hash className="h-8 w-8 text-muted-foreground" />
              )}
            </div>
            <p className="text-sm text-muted-foreground">
              No messages yet. Start the conversation!
            </p>
          </div>
        )}

        {groupedMessages.map((group) => (
          <div key={group.date}>
            {group.date && (
              <div className="my-4 flex items-center gap-3">
                <div className="h-px flex-1 bg-border" />
                <span className="text-xs text-muted-foreground">
                  {group.date}
                </span>
                <div className="h-px flex-1 bg-border" />
              </div>
            )}
            {group.messages.map((msg) => {
              const isOwn = msg.user?.username === currentUsername;
              const isEditing = editingId === msg.id;

              return (
                <div
                  key={msg.id}
                  className={cn(
                    "group mb-2 flex",
                    isOwn ? "justify-end" : "justify-start"
                  )}
                >
                  <div
                    className={cn(
                      "flex max-w-[70%] flex-col gap-1",
                      isOwn ? "items-end" : "items-start"
                    )}
                  >
                    {/* Username */}
                    {!isOwn && msg.user && (
                      <span className="px-1 text-xs font-medium text-muted-foreground">
                        {msg.user.username}
                      </span>
                    )}

                    <div
                      className={cn(
                        "relative rounded-2xl px-3.5 py-2",
                        isOwn
                          ? "bg-primary text-primary-foreground rounded-br-md"
                          : "bg-card text-card-foreground border border-border rounded-bl-md"
                      )}
                    >
                      {isEditing ? (
                        <div className="flex items-center gap-2">
                          <input
                            type="text"
                            value={editContent}
                            onChange={(e) => setEditContent(e.target.value)}
                            onKeyDown={(e) => {
                              if (e.key === "Enter") confirmEdit();
                              if (e.key === "Escape") cancelEdit();
                            }}
                            className="min-w-[150px] bg-transparent text-sm focus:outline-none"
                            autoFocus
                          />
                          <button
                            onClick={confirmEdit}
                            className="text-current opacity-70 hover:opacity-100"
                            aria-label="Confirm edit"
                          >
                            <Check className="h-3.5 w-3.5" />
                          </button>
                          <button
                            onClick={cancelEdit}
                            className="text-current opacity-70 hover:opacity-100"
                            aria-label="Cancel edit"
                          >
                            <X className="h-3.5 w-3.5" />
                          </button>
                        </div>
                      ) : (
                        <p className="text-sm leading-relaxed whitespace-pre-wrap break-words">
                          {msg.content}
                        </p>
                      )}

                      {/* Actions */}
                      {isOwn && !isEditing && (
                        <div className="absolute -top-3 right-0 hidden items-center gap-0.5 rounded-md border border-border bg-card p-0.5 shadow-sm group-hover:flex">
                          <button
                            onClick={() => startEdit(msg)}
                            className="flex h-6 w-6 items-center justify-center rounded text-muted-foreground transition-colors hover:text-foreground hover:bg-secondary"
                            aria-label="Edit message"
                          >
                            <Edit3 className="h-3 w-3" />
                          </button>
                          <button
                            onClick={() => onDeleteMessage(msg.id)}
                            className="flex h-6 w-6 items-center justify-center rounded text-muted-foreground transition-colors hover:text-destructive hover:bg-secondary"
                            aria-label="Delete message"
                          >
                            <Trash2 className="h-3 w-3" />
                          </button>
                        </div>
                      )}
                    </div>

                    {/* Meta */}
                    <div className="flex items-center gap-1.5 px-1">
                      <span className="text-[10px] text-muted-foreground">
                        {formatTime(msg.dateOfSend)}
                      </span>
                      {msg.edited && (
                        <span className="text-[10px] text-muted-foreground">
                          (edited)
                        </span>
                      )}
                    </div>
                  </div>
                </div>
              );
            })}
          </div>
        ))}
        <div ref={messagesEndRef} />
      </div>

      {/* Input */}
      <form
        onSubmit={handleSend}
        className="flex items-center gap-2 border-t border-border px-4 py-3"
      >
        <input
          ref={inputRef}
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder="Type a message..."
          className="flex-1 h-10 rounded-lg border border-input bg-card px-3 text-sm text-card-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
        />
        <button
          type="submit"
          disabled={!input.trim()}
          className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-primary-foreground transition-colors hover:bg-primary/90 disabled:opacity-50 disabled:cursor-not-allowed"
          aria-label="Send message"
        >
          <Send className="h-4 w-4" />
        </button>
      </form>
    </div>
  );
}
