import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import { Box, Button, Typography, Alert, CircularProgress, Card, CardContent } from "@mui/material";
import { getUser, deleteUser } from "../services/api";

export default function UserDelete() {
  const { userId } = useParams<{ userId: string }>();
  const navigate = useNavigate();
  const [user, setUser] = useState<{ user_id: string; first_name: string; last_name: string } | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    if (!userId) return;
    getUser(userId)
      .then((res) => setUser(res.data))
      .catch(() => setError("User not found"))
      .finally(() => setLoading(false));
  }, [userId]);

  const handleDelete = async () => {
    try {
      await deleteUser(userId!);
      navigate("/users");
    } catch {
      setError("Failed to delete user");
    }
  };

  if (loading) return <Box sx={{ p: 3, textAlign: "center" }}><CircularProgress /></Box>;

  return (
    <Box sx={{ p: 3, maxWidth: 600 }}>
      <Typography variant="h5" gutterBottom>Delete User</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      {user && (
        <Card>
          <CardContent>
            <Typography>Are you sure you want to delete user <strong>{user.user_id}</strong> ({user.first_name} {user.last_name})?</Typography>
            <Box sx={{ mt: 2, display: "flex", gap: 1 }}>
              <Button variant="contained" color="error" onClick={handleDelete}>Delete</Button>
              <Button variant="outlined" onClick={() => navigate("/users")}>Cancel</Button>
            </Box>
          </CardContent>
        </Card>
      )}
    </Box>
  );
}
