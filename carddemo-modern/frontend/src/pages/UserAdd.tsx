import { useState, type FormEvent } from "react";
import { useNavigate } from "react-router-dom";
import { Box, Button, TextField, Typography, Alert, MenuItem } from "@mui/material";
import { createUser } from "../services/api";

export default function UserAdd() {
  const navigate = useNavigate();
  const [error, setError] = useState("");
  const [saving, setSaving] = useState(false);
  const [form, setForm] = useState({
    user_id: "",
    first_name: "",
    last_name: "",
    password: "",
    user_type: "U",
  });

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      await createUser(form);
      navigate("/users");
    } catch (err: unknown) {
      const msg = (err as { response?: { data?: { detail?: string } } })?.response?.data?.detail || "Failed to create user";
      setError(msg);
    } finally {
      setSaving(false);
    }
  };

  return (
    <Box sx={{ p: 3, maxWidth: 600 }}>
      <Typography variant="h5" gutterBottom>Add User</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <form onSubmit={handleSubmit}>
        <TextField fullWidth label="User ID" value={form.user_id} onChange={(e) => setForm({ ...form, user_id: e.target.value })} margin="normal" required inputProps={{ maxLength: 8 }} />
        <TextField fullWidth label="First Name" value={form.first_name} onChange={(e) => setForm({ ...form, first_name: e.target.value })} margin="normal" inputProps={{ maxLength: 20 }} />
        <TextField fullWidth label="Last Name" value={form.last_name} onChange={(e) => setForm({ ...form, last_name: e.target.value })} margin="normal" inputProps={{ maxLength: 20 }} />
        <TextField fullWidth label="Password" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} margin="normal" required />
        <TextField select fullWidth label="User Type" value={form.user_type} onChange={(e) => setForm({ ...form, user_type: e.target.value })} margin="normal">
          <MenuItem value="U">Regular User</MenuItem>
          <MenuItem value="A">Admin</MenuItem>
        </TextField>
        <Box sx={{ mt: 2, display: "flex", gap: 1 }}>
          <Button type="submit" variant="contained" disabled={saving}>{saving ? "Creating..." : "Create User"}</Button>
          <Button variant="outlined" onClick={() => navigate("/users")}>Cancel</Button>
        </Box>
      </form>
    </Box>
  );
}
