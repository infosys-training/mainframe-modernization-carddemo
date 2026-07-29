// Format an ISO date string (YYYY-MM-DD) as MM/DD/YYYY to match the
// native date inputs used in the account forms. Returns "N/A" when empty.
export function formatDate(value: string | null | undefined): string {
  if (!value) return "N/A";
  const [y, m, d] = value.slice(0, 10).split("-");
  if (!y || !m || !d) return value;
  return `${m}/${d}/${y}`;
}
