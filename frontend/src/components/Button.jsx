import Loader from "./Loader";

/**
 * Primary button with optional loading state.
 * @param {{ loading?: boolean, children: React.ReactNode }} props
 */
const Button = ({ loading = false, children, disabled, ...rest }) => (
  <button
    className="btn-primary"
    disabled={disabled || loading}
    aria-busy={loading}
    {...rest}
  >
    {loading ? (
      <span className="btn-loading">
        <Loader size={18} color="#ffffff" />
        <span>Processing...</span>
      </span>
    ) : (
      children
    )}
  </button>
);

export default Button;
