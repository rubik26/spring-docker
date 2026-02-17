"use client";

import { useAuth } from "@/lib/auth-context";
import { AuthForm } from "@/components/auth-form";
import { Messenger } from "@/components/messenger";

export default function Home() {
  const { isLoggedIn } = useAuth();

  if (!isLoggedIn) {
    return <AuthForm />;
  }

  return <Messenger />;
}
