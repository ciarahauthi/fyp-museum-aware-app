import { useEffect, useState } from "react";

export default function TestDb() {
  const [text, setText] = useState("Loading...");
  const [error, setError] = useState("");

  useEffect(() => {
    fetch("/api/users/")
      .then(async (r) => {
        const t = await r.text();
        if (!r.ok) throw new Error(t);
        return t;
      })
      .then(setText)
      .catch((e) => setError(String(e)));
  }, []);

  return <pre>{error ? error : text}</pre>;
}