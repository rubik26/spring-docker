export interface User {
  id: number;
  username: string;
  password?: string;
  groups?: Group[];
  blacklist?: User[];
  blockedBy?: User[];
}

export interface Direct {
  id: number;
  sender: User;
  recipient: User;
  dateOfStart: string;
}

export interface DirectMessage {
  id: number;
  content: string;
  dateOfSend: string;
  edited: boolean;
  watched: boolean;
  user: User;
  direct: Direct;
}

export interface Group {
  id: number;
  name: string;
  description: string;
  avatar: string | null;
  avatarFileName: string | null;
  avatarFileType: string | null;
  dateOfCreate: string;
  admin: User;
  moderators: User[];
  participants: User[];
}

export interface GroupMessage {
  id: number;
  content: string;
  dateOfSend: string;
  edited: boolean;
  user: User;
  group: Group;
}

export interface AuthCredentials {
  username: string;
  password: string;
}
