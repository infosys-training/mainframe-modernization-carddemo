export function isRequired(value: string): string | null {
  return value.trim() ? null : 'This field is required';
}

export function maxLength(max: number) {
  return (value: string): string | null =>
    value.length <= max ? null : `Maximum ${max} characters allowed`;
}

export function isNumeric(value: string): string | null {
  return /^\d*$/.test(value) ? null : 'Must be numeric';
}

export function isDateYMD(value: string): string | null {
  if (!value) return null;
  return /^\d{4}-\d{2}-\d{2}$/.test(value) ? null : 'Date must be YYYY-MM-DD';
}

export function isDateMDY(value: string): string | null {
  if (!value) return null;
  return /^\d{2}\/\d{2}\/\d{4}$/.test(value) ? null : 'Date must be MM/DD/YYYY';
}

export function isCurrency(value: string): string | null {
  if (!value) return null;
  return /^-?\d{1,8}(\.\d{1,2})?$/.test(value) ? null : 'Invalid currency format (max -99999999.99)';
}

export function validateField(value: string, validators: ((v: string) => string | null)[]): string | null {
  for (const validator of validators) {
    const error = validator(value);
    if (error) return error;
  }
  return null;
}
