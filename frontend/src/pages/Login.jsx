import { Link } from "react-router-dom";
import "../css/auth.css";
import "../css/signup.css";

/** Placeholder Login page */
const Login = () => (
  <main className="signup-page">
    <div className="signup-card">
      <header className="signup-header">
        <h1>Welcome Back</h1>
        <p>Login page — coming soon.</p>
      </header>
      <footer className="signup-footer">
        Don&apos;t have an account?{" "}
        <Link to="/signup">Sign up</Link>
      </footer>
    </div>
  </main>
);

export default Login;
