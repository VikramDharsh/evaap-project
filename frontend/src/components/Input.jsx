/**
 * Reusable text/email/select input component.
 * @param {{ id: string, label: string, error?: string }} props
 */
const Input = ({ id, label, error, className = "", ...rest }) => (
  <div className="input-group">
    <label htmlFor={id} className="input-label">
      {label}
    </label>
    <input
      id={id}
      className={`input-field ${error ? "input-error" : ""} ${className}`}
      aria-describedby={error ? `${id}-error` : undefined}
      aria-invalid={!!error}
      {...rest}
    />
    {error && (
      <span id={`${id}-error`} className="error-message" role="alert">
        {error}
      </span>
    )}
  </div>
);

export default Input;
