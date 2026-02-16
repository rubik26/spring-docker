"use client";

import { useState } from "react";
import { useAuth } from "@/lib/auth-context";
import { Trash2, Pencil, Check, X, User as UserIcon } from "lucide-react";

interface MessageBubbleProps {
  id: number;
  content: string;
  username: string;
  date: string;
  isEdited: boolean;
  isOwnMessage: boolean;
  onDelete?: (id: number) => void;
  onEdit?: (id: number, content: string) => void;
}

export function MessageBubble({
  id,
  content,
  username,
  date,
  isEdited,
  isOwnMessage,
  onDelete,
  onEdit,
}: MessageBubbleProps) {
  const [isEditing, setIsEditing] = useState(false);
  const [editContent, setEditContent] = useState(content);

  const handleSaveEdit = () => {
    if (editContent.trim() && onEdit) {
      onEdit(id, editContent.trim());
      setIsEditing(false);
    }
  };

  return (
    <div
      className={`flex gap-3 px-4 py-1.5 ${
        isOwnMessage ? "flex-row-reverse" : ""
      }`}
    >
      <div
        className={`flex h-8 w-8 shrink-0 items-center justify-center rounded-full ${
          isOwnMessage
            ? "bg-primary/20 text-primary"
            : "bg-muted text-muted-foreground"
        }`}
      >
        <UserIcon className="h-4 w-4" />
      </div>

      <div
        className={`group flex max-w-[70%] flex-col gap-1 ${
          isOwnMessage ? "items-end" : "items-start"
        }`}
      >
        <div className="flex items-center gap-2">
          <span className="text-xs font-medium text-muted-foreground">
            {username}
          </span>
          <span className="text-xs text-muted-foreground/60">{date}</span>
          {isEdited && (
            <span className="text-xs text-muted-foreground/40 italic">
              (edited)
            </span>
          )}
        </div>

        {isEditing ? (
          <div className="flex w-full flex-col gap-2">
            <input
              type="text"
              value={editContent}
              onChange={(e) => setEditContent(e.target.value)}
              onKeyDown={(e) => {
                if (e.key === "Enter") handleSaveEdit();
                if (e.key === "Escape") setIsEditing(false);
              }}
              className="w-full rounded-lg border border-primary bg-input px-3 py-2 text-sm text-foreground outline-none"
              autoFocus
            />
            <div className="flex gap-1">
              <button
                onClick={handleSaveEdit}
                className="rounded p-1 text-accent hover:bg-accent/10"
                aria-label="Save edit"
              >
                <Check className="h-3.5 w-3.5" />
              </button>
              <button
                onClick={() => {
                  setIsEditing(false);
                  setEditContent(content);
                }}
                className="rounded p-1 text-muted-foreground hover:bg-muted"
                aria-label="Cancel edit"
              >
                <X className="h-3.5 w-3.5" />
              </button>
            </div>
          </div>
        ) : (
          <div className="flex items-end gap-1">
            <div
              className={`rounded-2xl px-4 py-2 text-sm leading-relaxed ${
                isOwnMessage
                  ? "rounded-tr-sm bg-message-own text-primary-foreground"
                  : "rounded-tl-sm bg-message-other text-foreground"
              }`}
            >
              {content}
            </div>

            {isOwnMessage && (
              <div className="flex items-center gap-0.5 opacity-0 transition-opacity group-hover:opacity-100">
                {onEdit && (
                  <button
                    onClick={() => setIsEditing(true)}
                    className="rounded p-1 text-muted-foreground hover:text-foreground"
                    aria-label="Edit message"
                  >
                    <Pencil className="h-3 w-3" />
                  </button>
                )}
                {onDelete && (
                  <button
                    onClick={() => onDelete(id)}
                    className="rounded p-1 text-muted-foreground hover:text-destructive"
                    aria-label="Delete message"
                  >
                    <Trash2 className="h-3 w-3" />
                  </button>
                )}
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
