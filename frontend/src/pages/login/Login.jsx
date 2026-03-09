import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { loginUser } from "../../services/auth";
import "./Login.css";

export default function Login() {
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    async function handleSubmit(e) {
        e.preventDefault();
        setError(null);

        try {
            const token = await loginUser(email, password);
            localStorage.setItem("token", token);
            navigate("/");
        } catch (err) {
            setError(err.message);
        }
    }

    return (
        <section className="login-container">
            <h2 className="login-title">
                Login
            </h2>
            <form className="login-form" onSubmit={handleSubmit}>
                <section className="login-field">
                    <label htmlFor="email">
                        Email
                    </label>
                    <input
                        id="email"
                        type="email"
                        value={email}
                        onChange={(e) => setEmail(e.target.value)}
                        required
                    />
                </section>
                <section className="login-field">
                    <label htmlFor="password">
                        Password
                    </label>
                    <input
                        id="password"
                        type="password"
                        value={password}
                        onChange={(e) => setPassword(e.target.value)}
                        required
                    />
                </section>

                {error && <p className="login-error">{error}</p>}

                <button type="submit" className="login-button">
                    Login
                </button>

            </form>
        </section>
    );
}
