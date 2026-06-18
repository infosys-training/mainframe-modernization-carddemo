import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Button,
  TextField,
  Typography,
  Alert,
  MenuItem,
} from "@mui/material";
import { addTransaction } from "../services/api";

const TRAN_TYPES = [
  { value: "01", label: "Purchase" },
  { value: "02", label: "Payment" },
  { value: "03", label: "Credit" },
  { value: "04", label: "Authorization" },
  { value: "05", label: "Refund" },
  { value: "06", label: "Reversal" },
  { value: "07", label: "Adjustment" },
];

export default function TransactionAdd() {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    tran_type_cd: "01",
    tran_cat_cd: 1,
    tran_source: "ONLINE",
    tran_desc: "",
    tran_amt: "",
    card_num: "",
    merchant_name: "",
    merchant_city: "",
    merchant_zip: "",
  });

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      await addTransaction({
        ...form,
        tran_amt: Number(form.tran_amt),
        tran_cat_cd: Number(form.tran_cat_cd),
      });
      navigate("/transactions");
    } catch {
      setError("Failed to add transaction");
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box sx={{ p: 3, maxWidth: 600 }}>
      <Typography variant="h5" gutterBottom>Add Transaction</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <form onSubmit={handleSubmit}>
        <TextField select fullWidth label="Transaction Type" value={form.tran_type_cd} onChange={(e) => setForm({ ...form, tran_type_cd: e.target.value })} margin="normal">
          {TRAN_TYPES.map((t) => <MenuItem key={t.value} value={t.value}>{t.label}</MenuItem>)}
        </TextField>
        <TextField fullWidth label="Card Number" value={form.card_num} onChange={(e) => setForm({ ...form, card_num: e.target.value })} margin="normal" required inputProps={{ maxLength: 16 }} />
        <TextField fullWidth label="Amount" type="number" value={form.tran_amt} onChange={(e) => setForm({ ...form, tran_amt: e.target.value })} margin="normal" required inputProps={{ step: "0.01" }} />
        <TextField fullWidth label="Description" value={form.tran_desc} onChange={(e) => setForm({ ...form, tran_desc: e.target.value })} margin="normal" inputProps={{ maxLength: 100 }} />
        <TextField fullWidth label="Merchant Name" value={form.merchant_name} onChange={(e) => setForm({ ...form, merchant_name: e.target.value })} margin="normal" />
        <TextField fullWidth label="Merchant City" value={form.merchant_city} onChange={(e) => setForm({ ...form, merchant_city: e.target.value })} margin="normal" />
        <TextField fullWidth label="Merchant ZIP" value={form.merchant_zip} onChange={(e) => setForm({ ...form, merchant_zip: e.target.value })} margin="normal" />
        <Box sx={{ mt: 2, display: "flex", gap: 1 }}>
          <Button type="submit" variant="contained" disabled={saving}>{saving ? "Adding..." : "Add Transaction"}</Button>
          <Button variant="outlined" onClick={() => navigate("/transactions")}>Cancel</Button>
        </Box>
      </form>
    </Box>
  );
}
