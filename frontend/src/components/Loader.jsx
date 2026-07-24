/**
 * Loader — a small inline spinner used inside buttons or standalone.
 * @param {{ size?: number, color?: string }} props
 */
const Loader = ({ size = 18, color = "#ffffff" }) => (
  <span
    className="loader"
    style={{
      width: size,
      height: size,
      borderColor: `${color}33`,
      borderTopColor: color,
    }}
    role="status"
    aria-label="Loading"
  />
);

export default Loader;
