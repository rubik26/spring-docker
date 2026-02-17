const API_BASE = "/api";

function getAuthHeaders(): HeadersInit {
  if (typeof window === "undefined") return {};
  const creds = localStorage.getItem("messenger_credentials");
  if (!creds) return {};
  return {
    Authorization: `Basic ${creds}`,
  };
}

async function request<T>(
  url: string,
  options: RequestInit = {}
): Promise<T> {
  const res = await fetch(`${API_BASE}${url}`, {
    ...options,
    headers: {
      "Content-Type": "application/json",
      ...getAuthHeaders(),
      ...options.headers,
    },
  });

  if (!res.ok) {
    const errorText = await res.text().catch(() => "Request failed");
    throw new Error(errorText || `HTTP ${res.status}`);
  }

  if (res.status === 204 || res.headers.get("content-length") === "0") {
    return undefined as T;
  }

  return res.json();
}

// Auth
export function saveCredentials(username: string, password: string) {
  const encoded = btoa(`${username}:${password}`);
  localStorage.setItem("messenger_credentials", encoded);
  localStorage.setItem("messenger_username", username);
}

export function getStoredUsername(): string | null {
  if (typeof window === "undefined") return null;
  return localStorage.getItem("messenger_username");
}

export function clearCredentials() {
  localStorage.removeItem("messenger_credentials");
  localStorage.removeItem("messenger_username");
}

export function isAuthenticated(): boolean {
  if (typeof window === "undefined") return false;
  return !!localStorage.getItem("messenger_credentials");
}

// Users
export interface User {
  id: number;
  username: string;
  password?: string;
}

export const userApi = {
  getUsers: () => request<User[]>("/users"),
  createUser: (user: { username: string; password: string }) =>
    request<User>("/createUser", {
      method: "POST",
      body: JSON.stringify(user),
    }),
  updateUser: (id: number, user: Partial<User>) =>
    request<User>(`/updateUser/${id}`, {
      method: "PUT",
      body: JSON.stringify(user),
    }),
  deleteUser: (id: number) =>
    request<void>(`/deleteUser/${id}`, { method: "DELETE" }),
};

// Directs
export interface Direct {
  id: number;
  dateOfStart: string;
  sender?: User;
  recipient?: User;
}

export const directApi = {
  getMyDirects: () => request<Direct[]>("/myDirects"),
  getDirectsByUsername: (username: string) =>
    request<Direct[]>(`/directs/${username}`),
  createDirect: (receiverId: number, direct: Partial<Direct> = {}) =>
    request<Direct>(`/users/${receiverId}/createDirect`, {
      method: "POST",
      body: JSON.stringify(direct),
    }),
};

// Direct Messages
export interface DirectMessage {
  id: number;
  content: string;
  dateOfSend: string;
  edited: boolean;
  watched: boolean;
  user?: User;
  direct?: Direct;
}

export const directMessageApi = {
  getMessages: (directId: number) =>
    request<DirectMessage[]>(`/directs/${directId}/messages`),
  getMessagesByUsername: (directId: number, username: string) =>
    request<DirectMessage[]>(`/directs/${directId}/${username}/messages`),
  createMessage: (directId: number, message: { content: string }) =>
    request<DirectMessage>(`/directs/${directId}/createMessage`, {
      method: "POST",
      body: JSON.stringify(message),
    }),
  editMessage: (id: number, message: { content: string }) =>
    request<DirectMessage>(`/editDirectMessage${id}`, {
      method: "PUT",
      body: JSON.stringify(message),
    }),
  deleteMessage: (id: number) =>
    request<void>(`/deleteDirectMessage/${id}`, { method: "DELETE" }),
};

// Groups
export interface Group {
  id: number;
  name: string;
  description: string;
  avatar?: string;
  avatarFileName?: string;
  avatarFileType?: string;
  dateOfCreate: string;
  admin?: User;
}

export const groupApi = {
  getGroups: () => request<Group[]>("/groups"),
  getGroupsByName: (name: string) => request<Group[]>(`/groups/${name}`),
  createGroup: (group: { name: string; description: string }) =>
    request<Group>("/createGroup", {
      method: "POST",
      body: JSON.stringify(group),
    }),
  updateGroup: (id: number, group: Partial<Group>) =>
    request<Group>(`/updateGroup/${id}`, {
      method: "PUT",
      body: JSON.stringify(group),
    }),
  deleteGroup: (id: number) =>
    request<void>(`/deleteGroup/${id}`, { method: "DELETE" }),
};

// Group Messages
export interface GroupMessage {
  id: number;
  content: string;
  dateOfSend: string;
  edited: boolean;
  user?: User;
  group?: Group;
}

export const groupMessageApi = {
  getMessages: (groupId: number) =>
    request<GroupMessage[]>(`/groups/${groupId}/messages`),
  getMessagesByUser: (groupId: number, username: string) =>
    request<GroupMessage[]>(`/groups/${groupId}/${username}/messages/`),
  createMessage: (groupId: number, message: { content: string }) =>
    request<GroupMessage>(`/groups/${groupId}/createMessage`, {
      method: "POST",
      body: JSON.stringify(message),
    }),
  editMessage: (id: number, message: { content: string }) =>
    request<GroupMessage>(`/editGroupMessage/${id}`, {
      method: "PUT",
      body: JSON.stringify(message),
    }),
  deleteMessage: (id: number) =>
    request<void>(`/deleteGroupMessage/${id}`, { method: "DELETE" }),
};

// Direct Message Images
export interface DirectMessageImage {
  id: number;
  imageName: string;
  imageData: string; // base64
  imageType: string;
}

export const directMessageImageApi = {
  getImages: () => request<DirectMessageImage[]>("/directImages"),
  uploadImage: async (messageId: number, file: File) => {
    const formData = new FormData();
    formData.append("imageFile", file);
    const res = await fetch(`${API_BASE}/uploadDirectImage/${messageId}`, {
      method: "POST",
      headers: { ...getAuthHeaders() },
      body: formData,
    });
    if (!res.ok) throw new Error("Upload failed");
    return res.json() as Promise<DirectMessageImage>;
  },
  deleteImage: (id: number) =>
    request<void>(`/deleteDirectImage/${id}`, { method: "DELETE" }),
};

// Group Message Images
export interface GroupMessageImage {
  id: number;
  imageName: string;
  imageData: string; // base64
  imageType: string;
}

export const groupMessageImageApi = {
  getImages: () => request<GroupMessageImage[]>("/groupimages"),
  uploadImage: async (messageId: number, file: File) => {
    const formData = new FormData();
    formData.append("imageFile", file);
    const res = await fetch(`${API_BASE}/uploadGroupImage/${messageId}`, {
      method: "POST",
      headers: { ...getAuthHeaders() },
      body: formData,
    });
    if (!res.ok) throw new Error("Upload failed");
    return res.json() as Promise<GroupMessageImage>;
  },
  deleteImage: (id: number) =>
    request<void>(`/deleteGroupImage/${id}`, { method: "DELETE" }),
};

// SWR fetcher with auth
export const fetcher = async (url: string) => {
  const res = await fetch(url, {
    headers: {
      ...getAuthHeaders(),
    },
  });
  if (!res.ok) throw new Error("Fetch failed");
  return res.json();
};
