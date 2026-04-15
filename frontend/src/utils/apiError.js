export function getApiErrorMessage(err, preferredField) {
  const data = err?.response?.data;
  const fieldErrors = data?.fieldErrors || {};

  if (preferredField && fieldErrors[preferredField]) {
    return fieldErrors[preferredField];
  }

  const firstFieldError = Object.values(fieldErrors)[0];
  if (firstFieldError) {
    return firstFieldError;
  }

  return data?.message || 'Something went wrong';
}
