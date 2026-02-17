import type {
  User,
  Direct,
  DirectMessage,
  GroupMessage,
  Group,
} from "./types";

function getAuthHeader(): string {
  if (typeof window === "undefined") return "";
  const creds = localStorage.getItem("auth_credentials");
  if (!creds) return "";
  const { username, password } = JSON.parse(creds);
  return "Basic " + btoa(`${username}:${password}`);
}

async function apiFetch<T>(
  url: string,
  options: RequestInit = {}
): Promise<T> {
  const headers: Record<string, string> = {
    ...(options.headers as Record<string, string>),
  };

  const auth = getAuthHeader();
  if (auth) {
    headers["Authorization"] = auth;
  }

  if (!(options.body instanceof FormData)) {
    headers["Content-Type"] = "application/json";
  }

  const res = await fetch(url, {
    ...options,
    headers,
  });

  if (!res.ok) {
    const text = await res.text().catch(() => "");
    throw new Error(text || `API Error: ${res.status}`);
  }

  const text = await res.text();
  if (!text) return undefined as T;
  return JSON.parse(text) as T;
}

// Auth
export function saveCredentials(username: string, password: string) {
  localStorage.setItem(
    "auth_credentials",
    JSON.stringify({ username, password })
  );
}

export function getStoredCredentials(): {
  username: string;
  password: string;
} | null {
  if (typeof window === "undefined") return null;
  const creds = localStorage.getItem("auth_credentials");
  if (!creds) return null;
  return JSON.parse(creds);
}

export function clearCredentials() {
  localStorage.removeItem("auth_credentials");
}

// Users
export const fetchUsers = (): Promise<User[]> => apiFetch("/api/users");

export const createUser = (user: {
  username: string;
  password: string;
}): Promise<User> =>
  apiFetch("/api/createUser", {
    method: "POST",
    body: JSON.stringify(user),
  });

export const updateUser = (id: number, user: Partial<User>): Promise<User> =>
  apiFetch(`/api/updateUser/${id}`, {
    method: "PUT",
    body: JSON.stringify(user),
  });

export const deleteUser = (id: number): Promise<void> =>
  apiFetch(`/api/deleteUser/${id}`, { method: "DELETE" });

// Directs
export const fetchDirects = (username: string): Promise<Direct[]> =>
  apiFetch(`/api/directs/${username}`);

export const createDirect = (direct: {
  sender: { id: number };
  recipient: { id: number };
}): Promise<Direct> =>
  apiFetch("/api/createDirect", {
    method: "POST",
    body: JSON.stringify(direct),
  });

// Direct Messages
export const fetchDirectMessages = (
  directId: number
): Promise<DirectMessage[]> =>
  apiFetch(`/api/directs/${directId}/messages`);

export const createDirectMessage = (
  directId: number,
  message: { content: string; user: { id: number } }
): Promise<DirectMessage> =>
  apiFetch(`/api/directs/${directId}/createMessage`, {
    method: "POST",
    body: JSON.stringify(message),
  });

export const deleteDirectMessage = (id: number): Promise<void> =>
  apiFetch(`/api/deleteDirectMessage/${id}`, { method: "DELETE" });

// Groups
export const fetchGroups = (): Promise<Group[]> => apiFetch("/api/groups");

export const fetchGroupsByName = (name: string): Promise<Group[]> =>
  apiFetch(`/api/groups/${name}`);

export const createGroup = (group: {
  name: string;
  description: string;
  admin: { id: number };
  participants: { id: number }[];
}): Promise<Group> =>
  apiFetch("/api/createGroup", {
    method: "POST",
    body: JSON.stringify(group),
  });

export const updateGroup = (
  id: number,
  group: Partial<Group>
): Promise<Group> =>
  apiFetch(`/api/updateGroup/${id}`, {
    method: "PUT",
    body: JSON.stringify(group),
  });

export const deleteGroup = (id: number): Promise<void> =>
  apiFetch(`/api/deleteGroup/${id}`, { method: "DELETE" });

// Group Messages
export const fetchGroupMessages = (
  groupId: number
): Promise<GroupMessage[]> =>
  apiFetch(`/api/groups/${groupId}/messages`);

export const createGroupMessage = (
  groupId: number,
  message: { content: string; user: { id: number } }
): Promise<GroupMessage> =>
  apiFetch(`/api/groups/${groupId}/createMessage`, {
    method: "POST",
    body: JSON.stringify(message),
  });

export const editGroupMessage = (
  id: number,
  message: Partial<GroupMessage>
): Promise<GroupMessage> =>
  apiFetch(`/api/editMessage/${id}`, {
    method: "PUT",
    body: JSON.stringify(message),
  });

export const deleteGroupMessage = (id: number): Promise<void> =>
  apiFetch(`/api/deleteMessage/${id}`, { method: "DELETE" });

// SWR fetcher
export const fetcher = (url: string) => apiFetch(url);
