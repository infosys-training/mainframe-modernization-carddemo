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
import { getCard, updateCard } from "../services/api";

export default function CardUpdate() {
  const { cardNum } = useParams<{ cardNum: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [form, setForm] = useState({
    embossed_name: "",
    active_status: "Y",
  });

  useEffect(() => {
    if (!cardNum) return;
    getCard(cardNum)
      .then((res) => {
        setForm({
          embossed_name: res.data.embossed_name,
          active_status: res.data.active_status,
        });
      })
      .catch(() => setError("Failed to load card"))
      .finally(() => setLoading(false));
  }, [cardNum]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      await updateCard(cardNum!, form);
      setSuccess("Card updated successfully");
      setTimeout(() => navigate(`/cards/${cardNum}`), 1000);
    } catch {
      setError("Failed to update card");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Box sx={{ p: 3, textAlign: "center" }}><CircularProgress /></Box>;

  return (
    <Box sx={{ p: 3, maxWidth: 600 }}>
      <Typography variant="h5" gutterBottom>Update Card {cardNum}</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
      <form onSubmit={handleSubmit}>
        <TextField fullWidth label="Embossed Name" value={form.embossed_name} onChange={(e) => setForm({ ...form, embossed_name: e.target.value })} margin="normal" inputProps={{ maxLength: 50 }} />
        <TextField select fullWidth label="Status" value={form.active_status} onChange={(e) => setForm({ ...form, active_status: e.target.value })} margin="normal">
          <MenuItem value="Y">Active</MenuItem>
          <MenuItem value="N">Inactive</MenuItem>
        </TextField>
        <Box sx={{ mt: 2, display: "flex", gap: 1 }}>
          <Button type="submit" variant="contained" disabled={saving}>{saving ? "Saving..." : "Save"}</Button>
          <Button variant="outlined" onClick={() => navigate(`/cards/${cardNum}`)}>Cancel</Button>
        </Box>
      </form>
    </Box>
  );
}
