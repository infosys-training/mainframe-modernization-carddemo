import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import {
  Box,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Typography,
  Button,
  IconButton,
} from "@mui/material";
import { Delete as DeleteIcon } from "@mui/icons-material";
import { getUsers, deleteUser } from "../services/api";
import PaginationControls from "../components/PaginationControls";

interface User {
  user_id: string;
  first_name: string;
  last_name: string;
  user_type: string;
}

export default function UserList() {
  const [users, setUsers] = useState<User[]>([]);
  const [page, setPage] = useState(1);
  const navigate = useNavigate();
  const pageSize = 20;

  const loadUsers = useCallback(() => {
    getUsers(page, pageSize).then((res) => setUsers(res.data));
  }, [page, pageSize]);

  useEffect(() => { loadUsers(); }, [loadUsers]);

  const handleDelete = async (userId: string) => {
    if (!confirm(`Delete user ${userId}?`)) return;
    try {
      await deleteUser(userId);
      loadUsers();
    } catch {
      alert("Failed to delete user");
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
        <Typography variant="h5">User Management</Typography>
        <Button variant="contained" onClick={() => navigate("/users/add")}>Add User</Button>
      </Box>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>User ID</TableCell>
              <TableCell>First Name</TableCell>
              <TableCell>Last Name</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.map((user) => (
              <TableRow key={user.user_id} hover>
                <TableCell>{user.user_id}</TableCell>
                <TableCell>{user.first_name}</TableCell>
                <TableCell>{user.last_name}</TableCell>
                <TableCell>{user.user_type === "A" ? "Admin" : "User"}</TableCell>
                <TableCell>
                  <Button size="small" onClick={() => navigate(`/users/${user.user_id}/edit`)}>Edit</Button>
                  <IconButton size="small" color="error" onClick={() => handleDelete(user.user_id)}>
                    <DeleteIcon fontSize="small" />
                  </IconButton>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <PaginationControls
        page={page}
        hasMore={users.length === pageSize}
        onPrev={() => setPage((p) => Math.max(1, p - 1))}
        onNext={() => setPage((p) => p + 1)}
      />
    </Box>
  );
}
