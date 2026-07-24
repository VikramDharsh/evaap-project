/**
 * Validation utility functions for the auth module.
 */

/** Checks that a value is not empty */
export const isRequired = (value) => value.trim() !== "";

/** Validates email format */
export const isValidEmail = (email) =>
  /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(email.trim());

/**
 * Validates password strength:
 * - Minimum 8 characters
 * - At least one uppercase letter
 * - At least one lowercase letter
 * - At least one digit
 * - At least one special character
 */
export const isStrongPassword = (password) =>
  /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?]).{8,}$/.test(
    password
  );

/** Checks that two password fields match */
export const doPasswordsMatch = (password, confirmPassword) =>
  password === confirmPassword;

/**
 * Validates the full signup form fields.
 * Returns an errors object — empty means valid.
 */
export const validateSignupForm = ({ fullName, email, password, confirmPassword, role }) => {
  const errors = {};

  if (!isRequired(fullName)) {
    errors.fullName = "Full name is required.";
  }

  if (!isRequired(email)) {
    errors.email = "Email is required.";
  } else if (!isValidEmail(email)) {
    errors.email = "Please enter a valid email address.";
  }

  if (!isRequired(password)) {
    errors.password = "Password is required.";
  } else if (!isStrongPassword(password)) {
    errors.password =
      "Password must be at least 8 characters and include uppercase, lowercase, number, and special character.";
  }

  if (!isRequired(confirmPassword)) {
    errors.confirmPassword = "Please confirm your password.";
  } else if (!doPasswordsMatch(password, confirmPassword)) {
    errors.confirmPassword = "Passwords do not match.";
  }

  if (!isRequired(role)) {
    errors.role = "Please select a role.";
  }

  return errors;
};
