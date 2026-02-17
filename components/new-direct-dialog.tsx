"use client";

import { useState } from "react";
import useSWR, { mutate } from "swr";
import { useAuth } from "@/lib/auth-context";
import { fetcher, createDirect } from "@/lib/api";
import type { User } from "@/lib/types";
import { X, Loader2, Search, User as UserIcon } from "lucide-react";

interface NewDirectDialogProps {
  onClose: () => void;
}

export function NewDirectDialog({ onClose }: NewDirectDialogProps) {
  const { user } = useAuth();
  const [searchQuery, setSearchQuery] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const { data: users = [] } = useSWR<User[]>("/api/users", fetcher);

  const filteredUsers = users.filter(
    (u) =>
      u.id !== user?.id &&
      u.username.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleSelectUser = async (selectedUser: User) => {
    if (!user) return;
    setLoading(true);
    setError("");

    try {
      await createDirect({
        sender: { id: user.id },
        recipient: { id: selectedUser.id },
      });
      mutate(`/api/directs/${user.username}`);
      onClose();
    } catch (err) {
      setError(
        err instanceof Error ? err.message : "Failed to start conversation"
      );
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-background/80 backdrop-blur-sm">
      <div className="w-full max-w-md rounded-xl border border-border bg-card p-6 shadow-lg">
        <div className="mb-4 flex items-center justify-between">
          <h2 className="text-lg font-semibold text-foreground">
            New Direct Message
          </h2>
          <button
            onClick={onClose}
            className="rounded-lg p-1 text-muted-foreground hover:text-foreground"
            aria-label="Close"
          >
            <X className="h-5 w-5" />
          </button>
        </div>

        <div className="mb-4 flex items-center gap-2 rounded-lg bg-muted px-3 py-2">
          <Search className="h-4 w-4 text-muted-foreground" />
          <input
            type="text"
            placeholder="Search users..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="flex-1 bg-transparent text-sm text-foreground placeholder:text-muted-foreground outline-none"
          />
        </div>

        {error && (
          <div className="mb-4 rounded-lg bg-destructive/10 px-3 py-2 text-sm text-destructive">
            {error}
          </div>
        )}

        <div className="max-h-64 overflow-y-auto">
          {filteredUsers.length === 0 && (
            <p className="py-8 text-center text-sm text-muted-foreground">
              No users found
            </p>
          )}
          {filteredUsers.map((u) => (
            <button
              key={u.id}
              onClick={() => handleSelectUser(u)}
              disabled={loading}
              className="flex w-full items-center gap-3 rounded-lg px-3 py-3 text-left transition-colors hover:bg-muted disabled:opacity-50"
            >
              <div className="flex h-9 w-9 items-center justify-center rounded-full bg-muted text-muted-foreground">
                <UserIcon className="h-4 w-4" />
              </div>
              <span className="text-sm font-medium text-foreground">
                {u.username}
              </span>
              {loading && <Loader2 className="ml-auto h-4 w-4 animate-spin text-muted-foreground" />}
            </button>
          ))}
        </div>
      </div>
    </div>
  );
}
