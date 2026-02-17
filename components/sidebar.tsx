"use client";

import { useState } from "react";
import { cn } from "@/lib/utils";
import { useAuth } from "@/lib/auth-context";
import type { Direct, Group, User } from "@/lib/api";
import {
  MessageCircle,
  Users,
  LogOut,
  Plus,
  Search,
  Hash,
  ChevronLeft,
} from "lucide-react";

interface SidebarProps {
  directs: Direct[];
  groups: Group[];
  users: User[];
  activeChat: { type: "direct" | "group"; id: number } | null;
  onSelectChat: (type: "direct" | "group", id: number) => void;
  onCreateDirect: (userId: number) => void;
  onCreateGroup: (name: string, description: string) => void;
  currentUsername: string;
  collapsed: boolean;
  onToggleCollapse: () => void;
  directNameMap: Record<number, string>;
}

export function Sidebar({
  directs,
  groups,
  users,
  activeChat,
  onSelectChat,
  onCreateDirect,
  onCreateGroup,
  currentUsername,
  collapsed,
  onToggleCollapse,
  directNameMap,
}: SidebarProps) {
  const { logout } = useAuth();
  const [tab, setTab] = useState<"directs" | "groups">("directs");
  const [showNewDirect, setShowNewDirect] = useState(false);
  const [showNewGroup, setShowNewGroup] = useState(false);
  const [searchQuery, setSearchQuery] = useState("");
  const [groupName, setGroupName] = useState("");
  const [groupDesc, setGroupDesc] = useState("");

  const filteredUsers = users.filter(
    (u) =>
      u.username !== currentUsername &&
      u.username.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const filteredGroups = groups.filter((g) =>
    g.name.toLowerCase().includes(searchQuery.toLowerCase())
  );

  const handleCreateGroup = () => {
    if (groupName.trim()) {
      onCreateGroup(groupName.trim(), groupDesc.trim());
      setGroupName("");
      setGroupDesc("");
      setShowNewGroup(false);
    }
  };

  if (collapsed) {
    return (
      <aside className="flex w-16 flex-col items-center border-r border-border bg-card py-4 gap-4">
        <button
          onClick={onToggleCollapse}
          className="flex h-10 w-10 items-center justify-center rounded-lg bg-primary text-primary-foreground"
          aria-label="Expand sidebar"
        >
          <MessageCircle className="h-5 w-5" />
        </button>
        <button
          onClick={() => {
            setTab("directs");
            onToggleCollapse();
          }}
          className={cn(
            "flex h-10 w-10 items-center justify-center rounded-lg transition-colors",
            tab === "directs"
              ? "bg-secondary text-secondary-foreground"
              : "text-muted-foreground hover:text-foreground"
          )}
          aria-label="Direct messages"
        >
          <MessageCircle className="h-5 w-5" />
        </button>
        <button
          onClick={() => {
            setTab("groups");
            onToggleCollapse();
          }}
          className={cn(
            "flex h-10 w-10 items-center justify-center rounded-lg transition-colors",
            tab === "groups"
              ? "bg-secondary text-secondary-foreground"
              : "text-muted-foreground hover:text-foreground"
          )}
          aria-label="Group chats"
        >
          <Users className="h-5 w-5" />
        </button>
        <div className="flex-1" />
        <button
          onClick={logout}
          className="flex h-10 w-10 items-center justify-center rounded-lg text-muted-foreground transition-colors hover:text-destructive"
          aria-label="Log out"
        >
          <LogOut className="h-5 w-5" />
        </button>
      </aside>
    );
  }

  return (
    <aside className="flex w-80 flex-col border-r border-border bg-card">
      {/* Header */}
      <div className="flex items-center justify-between border-b border-border px-4 py-3">
        <div className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-primary">
            <MessageCircle className="h-4 w-4 text-primary-foreground" />
          </div>
          <span className="text-sm font-semibold text-foreground">
            Messenger
          </span>
        </div>
        <div className="flex items-center gap-1">
          <button
            onClick={onToggleCollapse}
            className="flex h-8 w-8 items-center justify-center rounded-md text-muted-foreground transition-colors hover:text-foreground hover:bg-secondary"
            aria-label="Collapse sidebar"
          >
            <ChevronLeft className="h-4 w-4" />
          </button>
        </div>
      </div>

      {/* Tabs */}
      <div className="flex border-b border-border">
        <button
          onClick={() => setTab("directs")}
          className={cn(
            "flex flex-1 items-center justify-center gap-2 py-2.5 text-sm font-medium transition-colors",
            tab === "directs"
              ? "border-b-2 border-primary text-foreground"
              : "text-muted-foreground hover:text-foreground"
          )}
        >
          <MessageCircle className="h-4 w-4" />
          Directs
        </button>
        <button
          onClick={() => setTab("groups")}
          className={cn(
            "flex flex-1 items-center justify-center gap-2 py-2.5 text-sm font-medium transition-colors",
            tab === "groups"
              ? "border-b-2 border-primary text-foreground"
              : "text-muted-foreground hover:text-foreground"
          )}
        >
          <Users className="h-4 w-4" />
          Groups
        </button>
      </div>

      {/* Search */}
      <div className="px-3 py-2">
        <div className="relative">
          <Search className="absolute left-2.5 top-1/2 h-4 w-4 -translate-y-1/2 text-muted-foreground" />
          <input
            type="text"
            placeholder={
              tab === "directs" ? "Search users..." : "Search groups..."
            }
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
            className="h-9 w-full rounded-md border border-input bg-background pl-9 pr-3 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-1 focus:ring-ring"
          />
        </div>
      </div>

      {/* Content */}
      <div className="flex-1 overflow-y-auto">
        {tab === "directs" && (
          <>
            <div className="px-3 py-1.5">
              <button
                onClick={() => setShowNewDirect(!showNewDirect)}
                className="flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-sm text-muted-foreground transition-colors hover:bg-secondary hover:text-foreground"
              >
                <Plus className="h-4 w-4" />
                New conversation
              </button>
            </div>

            {showNewDirect && (
              <div className="border-b border-border px-3 pb-2">
                <p className="mb-1.5 px-2 text-xs font-medium text-muted-foreground">
                  Select a user
                </p>
                {filteredUsers.map((user) => (
                  <button
                    key={user.id}
                    onClick={() => {
                      onCreateDirect(user.id);
                      setShowNewDirect(false);
                    }}
                    className="flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-sm text-foreground transition-colors hover:bg-secondary"
                  >
                    <div className="flex h-7 w-7 items-center justify-center rounded-full bg-accent text-xs font-medium text-accent-foreground">
                      {user.username[0]?.toUpperCase()}
                    </div>
                    {user.username}
                  </button>
                ))}
                {filteredUsers.length === 0 && (
                  <p className="px-2 py-1.5 text-xs text-muted-foreground">
                    No users found
                  </p>
                )}
              </div>
            )}

            {directs.map((direct) => {
              const otherUsername = directNameMap[direct.id];
              const displayName = otherUsername || `Direct #${direct.id}`;
              const initial = otherUsername
                ? otherUsername[0]?.toUpperCase()
                : "?";

              return (
                <button
                  key={direct.id}
                  onClick={() => onSelectChat("direct", direct.id)}
                  className={cn(
                    "flex w-full items-center gap-3 px-3 py-2.5 transition-colors",
                    activeChat?.type === "direct" &&
                      activeChat.id === direct.id
                      ? "bg-secondary"
                      : "hover:bg-secondary/50"
                  )}
                >
                  <div className="flex h-9 w-9 items-center justify-center rounded-full bg-primary/15 text-sm font-semibold text-primary">
                    {initial}
                  </div>
                  <div className="flex flex-col items-start">
                    <span className="text-sm font-medium text-foreground">
                      {displayName}
                    </span>
                    <span className="text-xs text-muted-foreground">
                      {direct.dateOfStart || "Active"}
                    </span>
                  </div>
                </button>
              );
            })}

            {directs.length === 0 && !showNewDirect && (
              <div className="flex flex-col items-center gap-2 px-4 py-8 text-center">
                <MessageCircle className="h-8 w-8 text-muted-foreground/50" />
                <p className="text-sm text-muted-foreground">
                  No conversations yet
                </p>
                <p className="text-xs text-muted-foreground">
                  Start a new conversation above
                </p>
              </div>
            )}
          </>
        )}

        {tab === "groups" && (
          <>
            <div className="px-3 py-1.5">
              <button
                onClick={() => setShowNewGroup(!showNewGroup)}
                className="flex w-full items-center gap-2 rounded-md px-2 py-1.5 text-sm text-muted-foreground transition-colors hover:bg-secondary hover:text-foreground"
              >
                <Plus className="h-4 w-4" />
                Create group
              </button>
            </div>

            {showNewGroup && (
              <div className="border-b border-border px-3 pb-3">
                <div className="flex flex-col gap-2">
                  <input
                    type="text"
                    placeholder="Group name"
                    value={groupName}
                    onChange={(e) => setGroupName(e.target.value)}
                    className="h-8 rounded-md border border-input bg-background px-2 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-1 focus:ring-ring"
                  />
                  <input
                    type="text"
                    placeholder="Description (optional)"
                    value={groupDesc}
                    onChange={(e) => setGroupDesc(e.target.value)}
                    className="h-8 rounded-md border border-input bg-background px-2 text-sm text-foreground placeholder:text-muted-foreground focus:outline-none focus:ring-1 focus:ring-ring"
                  />
                  <button
                    onClick={handleCreateGroup}
                    disabled={!groupName.trim()}
                    className="h-8 rounded-md bg-primary text-sm font-medium text-primary-foreground transition-colors hover:bg-primary/90 disabled:opacity-50"
                  >
                    Create
                  </button>
                </div>
              </div>
            )}

            {(searchQuery ? filteredGroups : groups).map((group) => (
              <button
                key={group.id}
                onClick={() => onSelectChat("group", group.id)}
                className={cn(
                  "flex w-full items-center gap-3 px-3 py-2.5 transition-colors",
                  activeChat?.type === "group" &&
                    activeChat.id === group.id
                    ? "bg-secondary"
                    : "hover:bg-secondary/50"
                )}
              >
                <div className="flex h-9 w-9 items-center justify-center rounded-full bg-secondary text-sm font-medium text-secondary-foreground">
                  <Hash className="h-4 w-4" />
                </div>
                <div className="flex flex-col items-start">
                  <span className="text-sm font-medium text-foreground">
                    {group.name}
                  </span>
                  <span className="line-clamp-1 text-xs text-muted-foreground">
                    {group.description || "No description"}
                  </span>
                </div>
              </button>
            ))}

            {groups.length === 0 && !showNewGroup && (
              <div className="flex flex-col items-center gap-2 px-4 py-8 text-center">
                <Users className="h-8 w-8 text-muted-foreground/50" />
                <p className="text-sm text-muted-foreground">
                  No groups yet
                </p>
                <p className="text-xs text-muted-foreground">
                  Create a group above
                </p>
              </div>
            )}
          </>
        )}
      </div>

      {/* Footer */}
      <div className="flex items-center justify-between border-t border-border px-4 py-3">
        <div className="flex items-center gap-2">
          <div className="flex h-8 w-8 items-center justify-center rounded-full bg-accent text-sm font-medium text-accent-foreground">
            {currentUsername[0]?.toUpperCase()}
          </div>
          <span className="text-sm font-medium text-foreground">
            {currentUsername}
          </span>
        </div>
        <button
          onClick={logout}
          className="flex h-8 w-8 items-center justify-center rounded-md text-muted-foreground transition-colors hover:text-destructive hover:bg-secondary"
          aria-label="Log out"
        >
          <LogOut className="h-4 w-4" />
        </button>
      </div>
    </aside>
  );
}
