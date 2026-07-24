import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import Button from "../components/Button";
import Input from "../components/Input";
import PasswordInput from "../components/PasswordInput";
import "../css/auth.css";
import "../css/signup.css";
import { registerUser } from "../services/authService";
import { validateSignupForm } from "../utils/validators";

/** Initial blank form state */
const INITIAL_FORM = {
  fullName: "",
  email: "",
  password: "",
  confirmPassword: "",
  role: "",
};

const Signup = () => {
  const navigate = useNavigate();

  const [form, setForm] = useState(INITIAL_FORM);
  const [errors, setErrors] = useState({});
  const [loading, setLoading] = useState(false);
  const [successMsg, setSuccessMsg] = useState("");
  const [errorMsg, setErrorMsg] = useState("");

  /** Generic change handler for all fields */
  const handleChange = ({ target: { name, value } }) => {
    setForm((prev) => ({ ...prev, [name]: value }));
    // Clear individual field error on edit
    if (errors[name]) {
      setErrors((prev) => ({ ...prev, [name]: "" }));
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    // Clear global messages
    setSuccessMsg("");
    setErrorMsg("");

    // Validate
    const validationErrors = validateSignupForm(form);
    if (Object.keys(validationErrors).length > 0) {
      setErrors(validationErrors);
      return;
    }

    setLoading(true);

    try {
      // Build payload — do NOT include confirmPassword
      const payload = {
        fullName: form.fullName.trim(),
        email: form.email.trim(),
        password: form.password,
        role: form.role,
      };

      await registerUser(payload);

      setSuccessMsg("Registration successful. Redirecting to login...");
      setForm(INITIAL_FORM);
      setErrors({});

      // Redirect after 2 seconds
      setTimeout(() => navigate("/login"), 2000);
    } catch (err) {
      const message =
        err?.response?.data?.message || "Registration failed. Please try again.";
      setErrorMsg(message);
    } finally {
      setLoading(false);
    }
  };

  return (
    <main className="signup-page">
      <div className="signup-card" role="region" aria-label="Sign up form">
        <header className="signup-header">
          <h1>Create an Account</h1>
          <p>Fill in the details below to get started.</p>
        </header>

        {successMsg && (
          <div className="alert alert-success" role="status" aria-live="polite">
            {successMsg}
          </div>
        )}

        {errorMsg && (
          <div className="alert alert-error" role="alert" aria-live="assertive">
            {errorMsg}
          </div>
        )}

        <form
          className="signup-form"
          onSubmit={handleSubmit}
          noValidate
          aria-label="Registration form"
        >
          <Input
            id="fullName"
            name="fullName"
            label="Full Name"
            type="text"
            placeholder="John Doe"
            value={form.fullName}
            onChange={handleChange}
            error={errors.fullName}
            autoComplete="name"
            required
          />

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
            placeholder="Min. 8 characters"
            value={form.password}
            onChange={handleChange}
            error={errors.password}
            autoComplete="new-password"
            required
          />

          <PasswordInput
            id="confirmPassword"
            name="confirmPassword"
            label="Confirm Password"
            placeholder="Re-enter your password"
            value={form.confirmPassword}
            onChange={handleChange}
            error={errors.confirmPassword}
            autoComplete="new-password"
            required
          />

          {/* Role dropdown */}
          <div className="input-group">
            <label htmlFor="role" className="input-label">
              Role
            </label>
            <select
              id="role"
              name="role"
              className={`input-field ${errors.role ? "input-error" : ""}`}
              value={form.role}
              onChange={handleChange}
              aria-describedby={errors.role ? "role-error" : undefined}
              aria-invalid={!!errors.role}
              required
            >
              <option value="" disabled>
                Select a role
              </option>
              <option value="CANDIDATE">Candidate</option>
              <option value="EMPLOYER">Employer</option>
            </select>
            {errors.role && (
              <span id="role-error" className="error-message" role="alert">
                {errors.role}
              </span>
            )}
          </div>

          <Button type="submit" loading={loading}>
            Create Account
          </Button>
        </form>

        <footer className="signup-footer">
          Already have an account?{" "}
          <Link to="/login">Sign in</Link>
        </footer>
      </div>
    </main>
  );
};

export default Signup;
