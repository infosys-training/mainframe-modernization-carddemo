import { useEffect, useState } from "react";
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
} from "@mui/material";
import { getAccounts } from "../services/api";
import PaginationControls from "../components/PaginationControls";

interface Account {
  acct_id: number;
  active_status: string;
  curr_bal: string;
  credit_limit: string;
  open_date: string | null;
}

export default function AccountList() {
  const [accounts, setAccounts] = useState<Account[]>([]);
  const [page, setPage] = useState(1);
  const navigate = useNavigate();
  const pageSize = 20;

  useEffect(() => {
    getAccounts(page, pageSize).then((res) => setAccounts(res.data));
  }, [page]);

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>Accounts</Typography>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Account ID</TableCell>
              <TableCell>Status</TableCell>
              <TableCell align="right">Balance</TableCell>
              <TableCell align="right">Credit Limit</TableCell>
              <TableCell>Open Date</TableCell>
              <TableCell>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {accounts.map((acct) => (
              <TableRow key={acct.acct_id} hover>
                <TableCell>{acct.acct_id}</TableCell>
                <TableCell>{acct.active_status === "Y" ? "Active" : "Inactive"}</TableCell>
                <TableCell align="right">${Number(acct.curr_bal).toFixed(2)}</TableCell>
                <TableCell align="right">${Number(acct.credit_limit).toFixed(2)}</TableCell>
                <TableCell>{acct.open_date || "N/A"}</TableCell>
                <TableCell>
                  <Button size="small" onClick={() => navigate(`/accounts/${acct.acct_id}`)}>View</Button>
                  <Button size="small" onClick={() => navigate(`/accounts/${acct.acct_id}/edit`)}>Edit</Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <PaginationControls
        page={page}
        hasMore={accounts.length === pageSize}
        onPrev={() => setPage((p) => Math.max(1, p - 1))}
        onNext={() => setPage((p) => p + 1)}
      />
    </Box>
  );
}
