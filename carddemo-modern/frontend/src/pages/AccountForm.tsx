import { useEffect, useState, type FormEvent } from "react";
import { useParams, useNavigate } from "react-router-dom";
import axios from "axios";
import {
  Box,
  Button,
  TextField,
  Typography,
  MenuItem,
  Alert,
  Paper,
  Stack,
  CircularProgress,
} from "@mui/material";
import { createAccount, getAccount, updateAccount } from "../services/api";

type Mode = "new" | "edit";

interface Props {
  mode: Mode;
}

const EMPTY = {
  acct_id: "",
  active_status: "Y",
  curr_bal: "0.00",
  credit_limit: "0.00",
  cash_credit_limit: "0.00",
  open_date: "",
  expiration_date: "",
  addr_zip: "",
  group_id: "",
};

// Fields that cannot be changed once an account exists.
const LOCKED_IN_EDIT = new Set(["acct_id", "curr_bal", "open_date"]);

export default function AccountForm({ mode }: Props) {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEdit = mode === "edit";
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [form, setForm] = useState({ ...EMPTY });

  const today = new Date().toISOString().slice(0, 10);

  useEffect(() => {
    if (!isEdit || !id) return;
    let cancelled = false;
    getAccount(Number(id))
      .then((res) => {
        if (cancelled) return;
        const a = res.data;
        setForm({
          acct_id: String(a.acct_id),
          active_status: a.active_status,
          curr_bal: String(a.curr_bal),
          credit_limit: String(a.credit_limit),
          cash_credit_limit: String(a.cash_credit_limit),
          open_date: a.open_date ?? "",
          expiration_date: a.expiration_date ?? "",
          addr_zip: a.addr_zip ?? "",
          group_id: a.group_id ?? "",
        });
      })
      .catch(() => { if (!cancelled) setError("Failed to load account"); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [id, isEdit]);

  const disabled = (field: string) => isEdit && LOCKED_IN_EDIT.has(field);

  const validate = (): string | null => {
    if (!isEdit && !form.acct_id.trim()) return "Account ID is required";
    if (form.open_date && form.expiration_date && form.expiration_date <= form.open_date) {
      return "Expiration date must be after the open date";
    }
    if (!isEdit && form.expiration_date && form.expiration_date <= today) {
      return "Expiration date must be a future date";
    }
    return null;
  };

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    const validationError = validate();
    if (validationError) {
      setError(validationError);
      return;
    }
    setSaving(true);
    try {
      if (isEdit) {
        await updateAccount(Number(id), {
          active_status: form.active_status,
          credit_limit: Number(form.credit_limit),
          cash_credit_limit: Number(form.cash_credit_limit),
          expiration_date: form.expiration_date || null,
          addr_zip: form.addr_zip,
          group_id: form.group_id,
        });
        setSuccess("Account updated successfully");
        setTimeout(() => navigate(`/accounts/${id}`), 900);
      } else {
        await createAccount({
          acct_id: Number(form.acct_id),
          active_status: form.active_status,
          curr_bal: Number(form.curr_bal),
          credit_limit: Number(form.credit_limit),
          cash_credit_limit: Number(form.cash_credit_limit),
          open_date: form.open_date || null,
          expiration_date: form.expiration_date || null,
          addr_zip: form.addr_zip,
          group_id: form.group_id,
        });
        setSuccess("Account created successfully");
        setTimeout(() => navigate("/accounts"), 900);
      }
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.data?.detail) {
        setError(String(err.response.data.detail));
      } else {
        setError(isEdit ? "Failed to update account" : "Failed to create account");
      }
    } finally {
      setSaving(false);
    }
  };

  const set = (field: keyof typeof form) => (
    e: React.ChangeEvent<HTMLInputElement>
  ) => setForm({ ...form, [field]: e.target.value });

  const backTo = isEdit ? `/accounts/${id}` : "/accounts";

  if (loading) return <Box sx={{ p: 3, textAlign: "center" }}><CircularProgress /></Box>;

  const gridSx = {
    display: "grid",
    gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" },
    columnGap: 2,
    rowGap: 2,
  };

  return (
    <Box sx={{ p: 3, maxWidth: 720, mx: "auto" }}>
      <Stack
        direction="row"
        sx={{
          alignItems: "center",
          justifyContent: "space-between",
          mb: 2,
          pb: 1.5,
          borderBottom: "3px solid",
          borderBottomColor: "primary.main",
        }}
      >
        <Button onClick={() => navigate(backTo)}>&lt;&nbsp;&nbsp;Back</Button>
        <Typography variant="h5" sx={{ fontWeight: 700, color: "primary.main" }}>
          {isEdit ? `Edit Account ${id}` : "Add Account"}
        </Typography>
        <Box sx={{ width: 64 }} />
      </Stack>

      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}

      <Paper elevation={0} sx={{ p: 3, borderRadius: 2, bgcolor: "grey.50" }}>
        <form onSubmit={handleSubmit}>
          <Box sx={gridSx}>
            <TextField
              required={!isEdit}
              disabled={disabled("acct_id")}
              size="small"
              label="Account ID"
              type="number"
              value={form.acct_id}
              onChange={set("acct_id")}
              helperText={isEdit ? "Cannot be changed" : "Unique 11-digit account identifier"}
            />
            <TextField
              select
              size="small"
              label="Status"
              value={form.active_status}
              onChange={set("active_status")}
            >
              <MenuItem value="Y">Active</MenuItem>
              <MenuItem value="N">Inactive</MenuItem>
            </TextField>
            <TextField size="small" label="Current Balance" type="number" value={form.curr_bal} onChange={set("curr_bal")} disabled={disabled("curr_bal")} helperText={isEdit ? "System-maintained" : undefined} />
            <TextField size="small" label="Credit Limit" type="number" value={form.credit_limit} onChange={set("credit_limit")} />
            <TextField size="small" label="Cash Credit Limit" type="number" value={form.cash_credit_limit} onChange={set("cash_credit_limit")} />
            <TextField size="small" label="Group ID" value={form.group_id} onChange={set("group_id")} inputProps={{ maxLength: 10 }} />
            <TextField
              size="small"
              label="Open Date"
              type="date"
              value={form.open_date}
              onChange={set("open_date")}
              disabled={disabled("open_date")}
              slotProps={{ inputLabel: { shrink: true } }}
            />
            <TextField
              size="small"
              label="Expiration Date"
              type="date"
              value={form.expiration_date}
              onChange={set("expiration_date")}
              slotProps={{ inputLabel: { shrink: true }, htmlInput: { min: form.open_date || today } }}
            />
            <TextField size="small" label="ZIP Code" value={form.addr_zip} onChange={set("addr_zip")} inputProps={{ maxLength: 10 }} />
          </Box>
          <Box sx={{ mt: 3, display: "flex", justifyContent: "flex-end", gap: 1 }}>
            <Button variant="outlined" onClick={() => navigate(backTo)}>Cancel</Button>
            <Button type="submit" variant="contained" disabled={saving}>
              {saving ? "Saving..." : "Save"}
            </Button>
          </Box>
        </form>
      </Paper>
    </Box>
  );
}
