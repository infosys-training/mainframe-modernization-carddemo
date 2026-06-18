import { useEffect, useState, type FormEvent } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  TextField,
  Typography,
  MenuItem,
  CircularProgress,
  Alert,
} from "@mui/material";
import { getAccount, updateAccount } from "../services/api";

export default function AccountUpdate() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [form, setForm] = useState({
    active_status: "Y",
    credit_limit: "",
    cash_credit_limit: "",
    addr_zip: "",
    group_id: "",
  });

  useEffect(() => {
    if (!id) return;
    getAccount(Number(id))
      .then((res) => {
        const a = res.data;
        setForm({
          active_status: a.active_status,
          credit_limit: String(a.credit_limit),
          cash_credit_limit: String(a.cash_credit_limit),
          addr_zip: a.addr_zip,
          group_id: a.group_id,
        });
      })
      .catch(() => setError("Failed to load account"))
      .finally(() => setLoading(false));
  }, [id]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      await updateAccount(Number(id), {
        active_status: form.active_status,
        credit_limit: Number(form.credit_limit),
        cash_credit_limit: Number(form.cash_credit_limit),
        addr_zip: form.addr_zip,
        group_id: form.group_id,
      });
      setSuccess("Account updated successfully");
      setTimeout(() => navigate(`/accounts/${id}`), 1000);
    } catch {
      setError("Failed to update account");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Box sx={{ p: 3, textAlign: "center" }}><CircularProgress /></Box>;

  return (
    <Box sx={{ p: 3, maxWidth: 600 }}>
      <Typography variant="h5" gutterBottom>Update Account {id}</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
      <form onSubmit={handleSubmit}>
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
        <TextField fullWidth label="Credit Limit" type="number" value={form.credit_limit} onChange={(e) => setForm({ ...form, credit_limit: e.target.value })} margin="normal" />
        <TextField fullWidth label="Cash Credit Limit" type="number" value={form.cash_credit_limit} onChange={(e) => setForm({ ...form, cash_credit_limit: e.target.value })} margin="normal" />
        <TextField fullWidth label="ZIP Code" value={form.addr_zip} onChange={(e) => setForm({ ...form, addr_zip: e.target.value })} margin="normal" inputProps={{ maxLength: 10 }} />
        <TextField fullWidth label="Group ID" value={form.group_id} onChange={(e) => setForm({ ...form, group_id: e.target.value })} margin="normal" inputProps={{ maxLength: 10 }} />
        <Box sx={{ mt: 2, display: "flex", gap: 1 }}>
          <Button type="submit" variant="contained" disabled={saving}>
            {saving ? "Saving..." : "Save"}
          </Button>
          <Button variant="outlined" onClick={() => navigate(`/accounts/${id}`)}>Cancel</Button>
        </Box>
      </form>
    </Box>
  );
}
