import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import {
  Box,
  Button,
  TextField,
  Typography,
  MenuItem,
  Alert,
  Paper,
} from "@mui/material";
import { createAccount } from "../services/api";

export default function AccountCreate() {
  const navigate = useNavigate();
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [form, setForm] = useState({
    acct_id: "",
    active_status: "Y",
    curr_bal: "0.00",
    credit_limit: "0.00",
    cash_credit_limit: "0.00",
    open_date: "",
    expiration_date: "",
    addr_zip: "",
    group_id: "",
  });

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setError("");
    if (!form.acct_id.trim()) {
      setError("Account ID is required");
      return;
    }
    setSaving(true);
    try {
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
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.data?.detail) {
        setError(String(err.response.data.detail));
      } else {
        setError("Failed to create account");
      }
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box sx={{ p: 3, maxWidth: 640 }}>
      <Typography variant="h5" fontWeight={700} gutterBottom>
        Add Account
      </Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
      <Paper sx={{ p: 3 }}>
        <form onSubmit={handleSubmit}>
          <TextField
            fullWidth
            required
            label="Account ID"
            type="number"
            value={form.acct_id}
            onChange={(e) => setForm({ ...form, acct_id: e.target.value })}
            margin="normal"
            helperText="Unique 11-digit account identifier"
          />
          <TextField
            select
            fullWidth
            label="Status"
            value={form.active_status}
            onChange={(e) => setForm({ ...form, active_status: e.target.value })}
            margin="normal"
          >
            <MenuItem value="Y">Active</MenuItem>
            <MenuItem value="N">Inactive</MenuItem>
          </TextField>
          <TextField fullWidth label="Current Balance" type="number" value={form.curr_bal} onChange={(e) => setForm({ ...form, curr_bal: e.target.value })} margin="normal" />
          <TextField fullWidth label="Credit Limit" type="number" value={form.credit_limit} onChange={(e) => setForm({ ...form, credit_limit: e.target.value })} margin="normal" />
          <TextField fullWidth label="Cash Credit Limit" type="number" value={form.cash_credit_limit} onChange={(e) => setForm({ ...form, cash_credit_limit: e.target.value })} margin="normal" />
          <TextField fullWidth label="Open Date" type="date" value={form.open_date} onChange={(e) => setForm({ ...form, open_date: e.target.value })} margin="normal" slotProps={{ inputLabel: { shrink: true } }} />
          <TextField fullWidth label="Expiration Date" type="date" value={form.expiration_date} onChange={(e) => setForm({ ...form, expiration_date: e.target.value })} margin="normal" slotProps={{ inputLabel: { shrink: true } }} />
          <TextField fullWidth label="ZIP Code" value={form.addr_zip} onChange={(e) => setForm({ ...form, addr_zip: e.target.value })} margin="normal" inputProps={{ maxLength: 10 }} />
          <TextField fullWidth label="Group ID" value={form.group_id} onChange={(e) => setForm({ ...form, group_id: e.target.value })} margin="normal" inputProps={{ maxLength: 10 }} />
          <Box sx={{ mt: 2, display: "flex", gap: 1 }}>
            <Button type="submit" variant="contained" disabled={saving}>
              {saving ? "Saving..." : "Save"}
            </Button>
            <Button variant="outlined" onClick={() => navigate("/accounts")}>Cancel</Button>
          </Box>
        </form>
      </Paper>
    </Box>
  );
}
