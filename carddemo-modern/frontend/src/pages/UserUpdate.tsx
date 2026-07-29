import { useEffect, useState, type FormEvent } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Box, Button, TextField, Typography, MenuItem, CircularProgress, Alert } from "@mui/material";
import { getUser, updateUser } from "../services/api";

export default function UserUpdate() {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState("");
  const [success, setSuccess] = useState("");
  const [form, setForm] = useState({
    first_name: "",
    last_name: "",
    user_type: "U",
    password: "",
  });

  useEffect(() => {
    if (!userId) return;
    getUser(userId)
      .then((res) => {
        setForm({
          first_name: res.data.first_name,
          last_name: res.data.last_name,
          user_type: res.data.user_type,
          password: "",
        });
      })
      .catch(() => setError("Failed to load user"))
      .finally(() => setLoading(false));
  }, [userId]);

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault();
    setSaving(true);
    setError("");
    try {
      const data: Record<string, string> = {
        first_name: form.first_name,
        last_name: form.last_name,
        user_type: form.user_type,
      };
      if (form.password) data.password = form.password;
      await updateUser(userId!, data);
      setSuccess("User updated successfully");
      setTimeout(() => navigate("/users"), 1000);
    } catch {
      setError("Failed to update user");
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <Box sx={{ p: 3, textAlign: "center" }}><CircularProgress /></Box>;

  return (
    <Box sx={{ p: 3, maxWidth: 600 }}>
      <Typography variant="h5" gutterBottom>Update User {userId}</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {success && <Alert severity="success" sx={{ mb: 2 }}>{success}</Alert>}
      <form onSubmit={handleSubmit}>
        <TextField fullWidth label="First Name" value={form.first_name} onChange={(e) => setForm({ ...form, first_name: e.target.value })} margin="normal" inputProps={{ maxLength: 20 }} />
        <TextField fullWidth label="Last Name" value={form.last_name} onChange={(e) => setForm({ ...form, last_name: e.target.value })} margin="normal" inputProps={{ maxLength: 20 }} />
        <TextField select fullWidth label="User Type" value={form.user_type} onChange={(e) => setForm({ ...form, user_type: e.target.value })} margin="normal">
          <MenuItem value="U">Regular User</MenuItem>
          <MenuItem value="A">Admin</MenuItem>
        </TextField>
        <TextField fullWidth label="New Password (leave blank to keep)" type="password" value={form.password} onChange={(e) => setForm({ ...form, password: e.target.value })} margin="normal" />
        <Box sx={{ mt: 2, display: "flex", gap: 1 }}>
          <Button type="submit" variant="contained" disabled={saving}>{saving ? "Saving..." : "Save"}</Button>
          <Button variant="outlined" onClick={() => navigate("/users")}>Cancel</Button>
        </Box>
      </form>
    </Box>
  );
}
