/**
 * Sends login credentials to the API and returns the access token on success.
 * Throws an error with a message on failure.
 *
 * @param {string} email
 * @param {string} password
 * @returns {Promise<string>} access_token
 */
export async function loginUser(email, password) {
    const body = new URLSearchParams({ username: email, password });

    const res = await fetch("/api/auth/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: body.toString(),
    });

    if (!res.ok) {
        const data = await res.json();
        throw new Error(data.detail || "Login failed");
    }

    const data = await res.json();
    return data.access_token;
}

export async function getUser() {
    const token = localStorage.getItem("token");
    const res = await fetch("/api/auth/me", {
        headers: { Authorization: `Bearer ${token}` },
    });
    if (!res.ok) throw new Error("Failed to fetch user");
    return res.json();
}
