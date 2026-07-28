import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";

import Button from "../components/Button";
import Input from "../components/Input";
import PasswordInput from "../components/PasswordInput";

import "../css/auth.css";

import { loginUser } from "../services/authService";
import { validateLoginForm } from "../utils/validators";

const INITIAL_FORM = {
  email: "",
  password: "",
};

const Login = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState(INITIAL_FORM);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [errorMsg, setErrorMsg] = useState("");

  const handleChange = ({ target: { name, value } }) => {
    setForm((prev) => ({ ...prev, [name]: value }));

    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  

  const handleSubmit = async (e) => {
    e.preventDefault();

    setErrorMsg("");

    const validationErrors = validateLoginForm(form);

    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setLoading(true);

    try {
      const response = await loginUser({
        email: form.email.trim(),
        password: form.password,
      });

      localStorage.setItem(
        "accessToken",
        response.data.accessToken
      );

      localStorage.setItem(
        "refreshToken",
        response.data.refreshToken
      );

      localStorage.setItem(
        "user",
        JSON.stringify(response.data.user)
      );

      navigate("/dashboard");
    } catch (err) {
      const message =
        err?.response?.data?.message ||
        "Login failed. Please try again.";

      setErrorMsg(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="signup-page">
      <div className="signup-card">
        <header className="signup-header">
          <h1>Login</h1>
          <p>Sign in to continue.</p>
        </header>

        {errorMsg && (
          <div className="alert alert-error">
            {errorMsg}
          </div>
        )}

        <form className="signup-form" onSubmit={handleSubmit}>
          <Input
            id="email"
            name="email"
            label="Email Address"
            type="email"
            placeholder="you@example.com"
            value={form.email}
            onChange={handleChange}
            error={errors.email}
            autoComplete="email"
            required
          />

          <PasswordInput
            id="password"
            name="password"
            label="Password"
            placeholder="Enter your password"
            value={form.password}
            onChange={handleChange}
            error={errors.password}
            autoComplete="current-password"
            required
          />

          <Button type="submit" loading={loading}>
            Login
          </Button>
        </form>

        <footer className="signup-footer">
          Don't have an account?{" "}
          <Link to="/register">Sign Up</Link>
        </footer>
      </div>
    </main>
  );
};

export default Login;