"use client";

import { useState } from "react";
import useSWR from "swr";
import { useAuth } from "@/lib/auth-context";
import { fetcher } from "@/lib/api";
import type { Direct, Group } from "@/lib/types";
import {
  MessageSquare,
  Users,
  Hash,
  Plus,
  LogOut,
  Search,
  User as UserIcon,
} from "lucide-react";
import { CreateGroupDialog } from "./create-group-dialog";
import { NewDirectDialog } from "./new-direct-dialog";

interface ChatSidebarProps {
  activeChat: { type: "direct" | "group"; id: number } | null;
  onSelectChat: (chat: { type: "direct" | "group"; id: number }) => void;
}

export function ChatSidebar({ activeChat, onSelectChat }: ChatSidebarProps) {
  const { user, logout } = useAuth();
  const [tab, setTab] = useState<"directs" | "groups">("directs");
  const [searchQuery, setSearchQuery] = useState("");
  const [showCreateGroup, setShowCreateGroup] = useState(false);
  const [showNewDirect, setShowNewDirect] = useState(false);

  const { data: directs = [] } = useSWR<Direct[]>(
    user ? `/api/directs/${user.username}` : null,
    fetcher,
    { refreshInterval: 5000 }
  );

  const { data: groups = [] } = useSWR<Group[]>(
    user ? "/api/groups" : null,
    fetcher,
    { refreshInterval: 5000 }
  );

  const filteredDirects = directs.filter((d) => {
    const otherUser =
      d.sender.username === user?.username ? d.recipient : d.sender;
    return otherUser.username.toLowerCase().includes(searchQuery.toLowerCase());
  });

  const filteredGroups = groups.filter((g) =>
    g.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  return (
    <aside className="flex h-full w-80 flex-col border-r border-border bg-sidebar">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-border px-4 py-4">
        <div className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
            <MessageSquare className="h-4 w-4 text-primary-foreground" />
          </div>
          <span className="text-base font-semibold text-foreground">
            Messenger
          </span>
        </div>
        <button
          onClick={logout}
          className="rounded-lg p-2 text-muted-foreground transition-colors hover:bg-muted hover:text-foreground"
          aria-label="Log out"
        >
          <LogOut className="h-4 w-4" />
        </button>
      </div>

      {/* User info */}
      <div className="flex items-center gap-3 border-b border-border px-4 py-3">
        <div className="flex h-9 w-9 items-center justify-center rounded-full bg-primary/20 text-primary">
          <UserIcon className="h-4 w-4" />
        </div>
        <div className="flex-1 overflow-hidden">
          <p className="truncate text-sm font-medium text-foreground">
            {user?.username}
          </p>
          <p className="text-xs text-muted-foreground">Online</p>
        </div>
      </div>

      {/* Search */}
      <div className="px-3 py-3">
        <div className="flex items-center gap-2 rounded-lg bg-muted px-3 py-2">
          <Search className="h-4 w-4 text-muted-foreground" />
          <input
            type="text"
            placeholder="Search conversations..."
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="flex-1 bg-transparent text-sm text-foreground placeholder:text-muted-foreground outline-none"
          />
        </div>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-border px-3">
        <button
          onClick={() => setTab("directs")}
          className={`flex flex-1 items-center justify-center gap-2 border-b-2 px-3 py-2.5 text-sm font-medium transition-colors ${
            tab === "directs"
              ? "border-primary text-primary"
              : "border-transparent text-muted-foreground hover:text-foreground"
          }`}
        >
          <MessageSquare className="h-4 w-4" />
          Directs
        </button>
        <button
          onClick={() => setTab("groups")}
          className={`flex flex-1 items-center justify-center gap-2 border-b-2 px-3 py-2.5 text-sm font-medium transition-colors ${
            tab === "groups"
              ? "border-primary text-primary"
              : "border-transparent text-muted-foreground hover:text-foreground"
          }`}
        >
          <Users className="h-4 w-4" />
          Groups
        </button>
      </div>

      {/* Conversation list */}
      <div className="flex-1 overflow-y-auto">
        {tab === "directs" && (
          <div className="flex flex-col py-1">
            {filteredDirects.length === 0 && (
              <p className="px-4 py-8 text-center text-sm text-muted-foreground">
                No direct conversations yet
              </p>
            )}
            {filteredDirects.map((direct) => {
              const otherUser =
                direct.sender.username === user?.username
                  ? direct.recipient
                  : direct.sender;
              const isActive =
                activeChat?.type === "direct" && activeChat.id === direct.id;
              return (
                <button
                  key={direct.id}
                  onClick={() =>
                    onSelectChat({ type: "direct", id: direct.id })
                  }
                  className={`flex items-center gap-3 px-4 py-3 text-left transition-colors ${
                    isActive
                      ? "bg-primary/10 text-foreground"
                      : "text-foreground hover:bg-muted"
                  }`}
                >
                  <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-muted text-muted-foreground">
                    <UserIcon className="h-5 w-5" />
                  </div>
                  <div className="flex-1 overflow-hidden">
                    <p className="truncate text-sm font-medium">
                      {otherUser.username}
                    </p>
                    <p className="truncate text-xs text-muted-foreground">
                      Started {direct.dateOfStart}
                    </p>
                  </div>
                </button>
              );
            })}
          </div>
        )}
        {tab === "groups" && (
          <div className="flex flex-col py-1">
            {filteredGroups.length === 0 && (
              <p className="px-4 py-8 text-center text-sm text-muted-foreground">
                No groups found
              </p>
            )}
            {filteredGroups.map((group) => {
              const isActive =
                activeChat?.type === "group" && activeChat.id === group.id;
              return (
                <button
                  key={group.id}
                  onClick={() =>
                    onSelectChat({ type: "group", id: group.id })
                  }
                  className={`flex items-center gap-3 px-4 py-3 text-left transition-colors ${
                    isActive
                      ? "bg-primary/10 text-foreground"
                      : "text-foreground hover:bg-muted"
                  }`}
                >
                  <div className="flex h-10 w-10 shrink-0 items-center justify-center rounded-full bg-accent/20 text-accent">
                    <Hash className="h-5 w-5" />
                  </div>
                  <div className="flex-1 overflow-hidden">
                    <p className="truncate text-sm font-medium">{group.name}</p>
                    <p className="truncate text-xs text-muted-foreground">
                      {group.participants?.length || 0} members
                    </p>
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </div>

      {/* New conversation button */}
      <div className="border-t border-border p-3">
        <button
          onClick={() =>
            tab === "directs"
              ? setShowNewDirect(true)
              : setShowCreateGroup(true)
          }
          className="flex w-full items-center justify-center gap-2 rounded-lg bg-primary px-4 py-2.5 text-sm font-medium text-primary-foreground transition-opacity hover:opacity-90"
        >
          <Plus className="h-4 w-4" />
          {tab === "directs" ? "New Message" : "New Group"}
        </button>
      </div>

      {showCreateGroup && (
        <CreateGroupDialog onClose={() => setShowCreateGroup(false)} />
      )}
      {showNewDirect && (
        <NewDirectDialog onClose={() => setShowNewDirect(false)} />
      )}
    </aside>
  );
}
