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
  Paperclip,
  Pencil,
  CheckCheck,
  Image as ImageIcon,
  Reply,
  CornerDownRight,
} from "lucide-react";

interface MessageImage {
  id: number;
  imageName: string;
  imageData: string;
  imageType: string;
}

interface Message {
  id: number;
  content: string;
  dateOfSend: string;
  edited: boolean;
  watched?: boolean;
  user?: { id: number; username: string };
  images?: MessageImage[];
  isOwn?: boolean;
}

// Reply encoding format embedded in content:
// >>reply:messageId:username:snippet<< actual message content
const REPLY_REGEX = /^>>reply:(\d+):([^:]*):(.*)<<\n?([\s\S]*)$/;

function parseReply(content: string): {
  replyToId: number;
  replyToUser: string;
  replySnippet: string;
  actualContent: string;
} | null {
  const match = content.match(REPLY_REGEX);
  if (!match) return null;
  return {
    replyToId: parseInt(match[1], 10),
    replyToUser: match[2],
    replySnippet: match[3],
    actualContent: match[4] || "",
  };
}

export function encodeReply(
  replyToId: number,
  replyToUser: string,
  replySnippet: string,
  content: string
): string {
  const snippet = replySnippet.slice(0, 80).replace(/\n/g, " ");
  return `>>reply:${replyToId}:${replyToUser}:${snippet}<<\n${content}`;
}

interface ChatViewProps {
  type: "direct" | "group";
  chatId: number;
  chatName: string;
  messages: Message[];
  currentUsername: string;
  onSendMessage: (content: string, imageFile?: File) => void;
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
  const [selectedFile, setSelectedFile] = useState<File | null>(null);
  const [filePreview, setFilePreview] = useState<string | null>(null);
  const [replyingTo, setReplyingTo] = useState<Message | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [expandedImage, setExpandedImage] = useState<string | null>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: "smooth" });
  }, [messages]);

  useEffect(() => {
    inputRef.current?.focus();
  }, [chatId]);

  // Clear reply when chat changes
  useEffect(() => {
    setReplyingTo(null);
    setEditingId(null);
    setEditContent("");
  }, [chatId]);

  useEffect(() => {
    if (selectedFile) {
      const url = URL.createObjectURL(selectedFile);
      setFilePreview(url);
      return () => URL.revokeObjectURL(url);
    }
    setFilePreview(null);
  }, [selectedFile]);

  const handleSend = (e: React.FormEvent) => {
    e.preventDefault();
    if (input.trim() || selectedFile) {
      let content = input.trim();
      if (replyingTo) {
        const replyUser =
          replyingTo.isOwn
            ? currentUsername
            : replyingTo.user?.username || chatName;
        // Get clean content (strip any nested reply prefix from the original)
        const parsed = parseReply(replyingTo.content);
        const originalText = parsed
          ? parsed.actualContent
          : replyingTo.content;
        content = encodeReply(
          replyingTo.id,
          replyUser,
          originalText,
          content
        );
      }
      onSendMessage(content, selectedFile || undefined);
      setInput("");
      setSelectedFile(null);
      setReplyingTo(null);
    }
  };

  const handleFileSelect = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file && file.type.startsWith("image/")) {
      setSelectedFile(file);
    }
    if (fileInputRef.current) {
      fileInputRef.current.value = "";
    }
  };

  const removeSelectedFile = () => {
    setSelectedFile(null);
  };

  const handleReply = (msg: Message) => {
    setReplyingTo(msg);
    setEditingId(null);
    inputRef.current?.focus();
  };

  const cancelReply = () => {
    setReplyingTo(null);
  };

  const startEdit = (msg: Message) => {
    setEditingId(msg.id);
    // When editing, use only the actual content (strip reply prefix)
    const parsed = parseReply(msg.content);
    setEditContent(parsed ? parsed.actualContent : msg.content);
    setReplyingTo(null);
  };

  const cancelEdit = () => {
    setEditingId(null);
    setEditContent("");
  };

  const confirmEdit = () => {
    if (editingId !== null && editContent.trim()) {
      // Preserve existing reply prefix if present
      const original = messages.find((m) => m.id === editingId);
      const parsed = original ? parseReply(original.content) : null;
      let newContent = editContent.trim();
      if (parsed) {
        newContent = encodeReply(
          parsed.replyToId,
          parsed.replyToUser,
          parsed.replySnippet,
          newContent
        );
      }
      onEditMessage(editingId, newContent);
      cancelEdit();
    }
  };

  const scrollToMessage = (messageId: number) => {
    const el = document.getElementById(`msg-${messageId}`);
    if (el) {
      el.scrollIntoView({ behavior: "smooth", block: "center" });
      el.classList.add("ring-2", "ring-primary/40");
      setTimeout(() => {
        el.classList.remove("ring-2", "ring-primary/40");
      }, 1500);
    }
  };

  const formatTime = (dateStr: string) => {
    if (!dateStr) return "";
    try {
      const date = new Date(dateStr);
      return date.toLocaleTimeString([], {
        hour: "2-digit",
        minute: "2-digit",
      });
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

  const chatInitial = chatName?.[0]?.toUpperCase() || "?";

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
        <div
          className={cn(
            "flex h-9 w-9 items-center justify-center rounded-full text-sm font-semibold",
            type === "direct"
              ? "bg-primary/15 text-primary"
              : "bg-secondary text-secondary-foreground"
          )}
        >
          {type === "direct" ? chatInitial : <Hash className="h-4 w-4" />}
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
              const isOwn =
                msg.isOwn ?? msg.user?.username === currentUsername;
              const isEditing = editingId === msg.id;
              const replyData = parseReply(msg.content);
              const displayContent = replyData
                ? replyData.actualContent
                : msg.content;

              return (
                <div
                  key={msg.id}
                  id={`msg-${msg.id}`}
                  className={cn(
                    "group mb-3 flex transition-all duration-300 rounded-lg",
                    isOwn ? "justify-end" : "justify-start"
                  )}
                >
                  {/* Avatar for other user */}
                  {!isOwn && (
                    <div className="mr-2 mt-5 flex h-7 w-7 flex-shrink-0 items-center justify-center rounded-full bg-primary/15 text-xs font-semibold text-primary">
                      {msg.user?.username?.[0]?.toUpperCase() ||
                        chatName?.[0]?.toUpperCase() ||
                        "?"}
                    </div>
                  )}

                  <div
                    className={cn(
                      "flex max-w-[70%] flex-col gap-1",
                      isOwn ? "items-end" : "items-start"
                    )}
                  >
                    {/* Username label */}
                    {!isOwn && (
                      <span className="px-1 text-xs font-medium text-primary">
                        {msg.user?.username || chatName}
                      </span>
                    )}
                    {isOwn && (
                      <span className="px-1 text-xs font-medium text-muted-foreground">
                        You
                      </span>
                    )}

                    <div
                      className={cn(
                        "relative rounded-2xl px-3.5 py-2",
                        isOwn
                          ? "bg-primary text-primary-foreground rounded-br-sm"
                          : "bg-card text-card-foreground border border-border rounded-bl-sm"
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
                        <>
                          {/* Reply quote block */}
                          {replyData && (
                            <button
                              type="button"
                              onClick={() =>
                                scrollToMessage(replyData.replyToId)
                              }
                              className={cn(
                                "mb-2 flex w-full items-start gap-2 rounded-lg px-2.5 py-1.5 text-left transition-colors",
                                isOwn
                                  ? "bg-primary-foreground/15 hover:bg-primary-foreground/25"
                                  : "bg-muted/60 hover:bg-muted"
                              )}
                            >
                              <div
                                className={cn(
                                  "mt-0.5 h-full w-0.5 flex-shrink-0 self-stretch rounded-full",
                                  isOwn
                                    ? "bg-primary-foreground/50"
                                    : "bg-primary/60"
                                )}
                              />
                              <div className="flex flex-col gap-0.5 overflow-hidden">
                                <span
                                  className={cn(
                                    "text-[11px] font-semibold",
                                    isOwn
                                      ? "text-primary-foreground/80"
                                      : "text-primary/80"
                                  )}
                                >
                                  {replyData.replyToUser}
                                </span>
                                <span
                                  className={cn(
                                    "truncate text-xs",
                                    isOwn
                                      ? "text-primary-foreground/60"
                                      : "text-muted-foreground"
                                  )}
                                >
                                  {replyData.replySnippet || "Image"}
                                </span>
                              </div>
                            </button>
                          )}

                          {/* Images */}
                          {msg.images &&
                            msg.images.length > 0 &&
                            msg.images.map((img) => (
                              <button
                                key={img.id}
                                type="button"
                                onClick={() =>
                                  setExpandedImage(
                                    `data:${img.imageType};base64,${img.imageData}`
                                  )
                                }
                                className="mb-1.5 block overflow-hidden rounded-lg"
                              >
                                <img
                                  src={`data:${img.imageType};base64,${img.imageData}`}
                                  alt={img.imageName}
                                  className="max-h-48 max-w-full rounded-lg object-cover"
                                />
                              </button>
                            ))}
                          {displayContent && (
                            <p className="text-sm leading-relaxed whitespace-pre-wrap break-words">
                              {displayContent}
                            </p>
                          )}
                        </>
                      )}

                      {/* Actions */}
                      {!isEditing && (
                        <div
                          className={cn(
                            "absolute -top-3 hidden items-center gap-0.5 rounded-md border border-border bg-card p-0.5 shadow-sm group-hover:flex",
                            isOwn ? "right-0" : "left-0"
                          )}
                        >
                          <button
                            onClick={() => handleReply(msg)}
                            className="flex h-6 w-6 items-center justify-center rounded text-muted-foreground transition-colors hover:text-foreground hover:bg-secondary"
                            aria-label="Reply to message"
                            title="Reply"
                          >
                            <Reply className="h-3 w-3" />
                          </button>
                          {isOwn && (
                            <>
                              <button
                                onClick={() => startEdit(msg)}
                                className="flex h-6 w-6 items-center justify-center rounded text-muted-foreground transition-colors hover:text-foreground hover:bg-secondary"
                                aria-label="Edit message"
                                title="Edit"
                              >
                                <Edit3 className="h-3 w-3" />
                              </button>
                              <button
                                onClick={() => onDeleteMessage(msg.id)}
                                className="flex h-6 w-6 items-center justify-center rounded text-muted-foreground transition-colors hover:text-destructive hover:bg-secondary"
                                aria-label="Delete message"
                                title="Delete"
                              >
                                <Trash2 className="h-3 w-3" />
                              </button>
                            </>
                          )}
                        </div>
                      )}
                    </div>

                    {/* Meta row: time + edited icon + watched icon */}
                    <div className="flex items-center gap-1.5 px-1">
                      <span className="text-[10px] text-muted-foreground">
                        {formatTime(msg.dateOfSend)}
                      </span>
                      {msg.edited && (
                        <span
                          title="Edited"
                          className="flex items-center text-muted-foreground"
                        >
                          <Pencil className="h-2.5 w-2.5" />
                        </span>
                      )}
                      {isOwn && type === "direct" && (
                        <span
                          title={msg.watched ? "Seen" : "Sent"}
                          className={cn(
                            "flex items-center",
                            msg.watched
                              ? "text-primary"
                              : "text-muted-foreground"
                          )}
                        >
                          <CheckCheck className="h-3 w-3" />
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

      {/* Image lightbox */}
      {expandedImage && (
        <div
          className="fixed inset-0 z-50 flex items-center justify-center bg-background/80 backdrop-blur-sm"
          onClick={() => setExpandedImage(null)}
          role="dialog"
          aria-label="Image preview"
        >
          <button
            onClick={() => setExpandedImage(null)}
            className="absolute right-4 top-4 flex h-10 w-10 items-center justify-center rounded-full bg-card text-foreground shadow-lg"
            aria-label="Close image"
          >
            <X className="h-5 w-5" />
          </button>
          <img
            src={expandedImage}
            alt="Expanded message image"
            className="max-h-[85vh] max-w-[90vw] rounded-lg object-contain shadow-2xl"
            onClick={(e) => e.stopPropagation()}
          />
        </div>
      )}

      {/* Reply preview bar */}
      {replyingTo && (
        <div className="flex items-center gap-3 border-t border-border bg-card px-4 py-2.5">
          <CornerDownRight className="h-4 w-4 flex-shrink-0 text-primary" />
          <div className="flex min-w-0 flex-1 items-center gap-2">
            <div className="h-8 w-0.5 flex-shrink-0 rounded-full bg-primary" />
            <div className="flex min-w-0 flex-col">
              <span className="text-xs font-semibold text-primary">
                {replyingTo.isOwn
                  ? "You"
                  : replyingTo.user?.username || chatName}
              </span>
              <span className="truncate text-xs text-muted-foreground">
                {(() => {
                  const parsed = parseReply(replyingTo.content);
                  const text = parsed
                    ? parsed.actualContent
                    : replyingTo.content;
                  return text || "Image";
                })()}
              </span>
            </div>
          </div>
          <button
            onClick={cancelReply}
            className="flex h-6 w-6 flex-shrink-0 items-center justify-center rounded-md text-muted-foreground transition-colors hover:text-foreground hover:bg-secondary"
            aria-label="Cancel reply"
          >
            <X className="h-4 w-4" />
          </button>
        </div>
      )}

      {/* File preview strip */}
      {selectedFile && filePreview && (
        <div className="flex items-center gap-2 border-t border-border bg-card px-4 py-2">
          <div className="relative h-14 w-14 flex-shrink-0 overflow-hidden rounded-lg border border-border">
            <img
              src={filePreview}
              alt="Selected file preview"
              className="h-full w-full object-cover"
            />
            <button
              onClick={removeSelectedFile}
              className="absolute -right-1 -top-1 flex h-5 w-5 items-center justify-center rounded-full bg-destructive text-destructive-foreground shadow"
              aria-label="Remove attachment"
            >
              <X className="h-3 w-3" />
            </button>
          </div>
          <div className="flex flex-col">
            <span className="text-xs font-medium text-foreground">
              {selectedFile.name}
            </span>
            <span className="text-[10px] text-muted-foreground">
              {(selectedFile.size / 1024).toFixed(1)} KB
            </span>
          </div>
        </div>
      )}

      {/* Input */}
      <form
        onSubmit={handleSend}
        className="flex items-center gap-2 border-t border-border px-4 py-3"
      >
        <input
          ref={fileInputRef}
          type="file"
          accept="image/*"
          onChange={handleFileSelect}
          className="hidden"
          aria-label="Attach image"
        />
        <button
          type="button"
          onClick={() => fileInputRef.current?.click()}
          className={cn(
            "flex h-10 w-10 flex-shrink-0 items-center justify-center rounded-lg transition-colors",
            selectedFile
              ? "bg-primary/15 text-primary"
              : "text-muted-foreground hover:text-foreground hover:bg-secondary"
          )}
          aria-label="Attach image"
        >
          {selectedFile ? (
            <ImageIcon className="h-4 w-4" />
          ) : (
            <Paperclip className="h-4 w-4" />
          )}
        </button>
        <input
          ref={inputRef}
          type="text"
          value={input}
          onChange={(e) => setInput(e.target.value)}
          placeholder={
            replyingTo ? "Type your reply..." : "Type a message..."
          }
          className="h-10 flex-1 rounded-lg border border-input bg-card px-3 text-sm text-card-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-2 focus:ring-ring"
        />
        <button
          type="submit"
          disabled={!input.trim() && !selectedFile}
          className="flex h-10 w-10 flex-shrink-0 items-center justify-center rounded-lg bg-primary text-primary-foreground transition-colors hover:bg-primary/90 disabled:cursor-not-allowed disabled:opacity-50"
          aria-label="Send message"
        >
          <Send className="h-4 w-4" />
        </button>
      </form>
    </div>
  );
}
