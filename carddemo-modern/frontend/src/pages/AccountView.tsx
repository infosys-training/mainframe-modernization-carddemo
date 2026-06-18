import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  CircularProgress,
  Table,
  TableBody,
  TableRow,
  TableCell,
} from "@mui/material";
import { getAccount } from "../services/api";
import ErrorMessage from "../components/ErrorMessage";

interface Account {
  acct_id: number;
  active_status: string;
  curr_bal: string;
  credit_limit: string;
  cash_credit_limit: string;
  open_date: string | null;
  expiration_date: string | null;
  reissue_date: string | null;
  curr_cyc_credit: string;
  curr_cyc_debit: string;
  addr_zip: string;
  group_id: string;
}

export default function AccountView() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [account, setAccount] = useState<Account | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!id) return;
    let cancelled = false;
    getAccount(Number(id))
      .then((res) => { if (!cancelled) setAccount(res.data); })
      .catch(() => { if (!cancelled) setError("Failed to load account"); })
      .finally(() => { if (!cancelled) setLoading(false); });
    return () => { cancelled = true; };
  }, [id]);

  if (loading) return <Box sx={{ p: 3, textAlign: "center" }}><CircularProgress /></Box>;

  return (
    <Box sx={{ p: 3 }}>
      <ErrorMessage message={error} onClose={() => setError(null)} />
      <Typography variant="h5" gutterBottom>Account Details</Typography>
      {account && (
        <Card>
          <CardContent>
            <Table>
              <TableBody>
                <TableRow><TableCell>Account ID</TableCell><TableCell>{account.acct_id}</TableCell></TableRow>
                <TableRow><TableCell>Status</TableCell><TableCell>{account.active_status === "Y" ? "Active" : "Inactive"}</TableCell></TableRow>
                <TableRow><TableCell>Current Balance</TableCell><TableCell>${Number(account.curr_bal).toFixed(2)}</TableCell></TableRow>
                <TableRow><TableCell>Credit Limit</TableCell><TableCell>${Number(account.credit_limit).toFixed(2)}</TableCell></TableRow>
                <TableRow><TableCell>Cash Credit Limit</TableCell><TableCell>${Number(account.cash_credit_limit).toFixed(2)}</TableCell></TableRow>
                <TableRow><TableCell>Open Date</TableCell><TableCell>{account.open_date || "N/A"}</TableCell></TableRow>
                <TableRow><TableCell>Expiration Date</TableCell><TableCell>{account.expiration_date || "N/A"}</TableCell></TableRow>
                <TableRow><TableCell>Reissue Date</TableCell><TableCell>{account.reissue_date || "N/A"}</TableCell></TableRow>
                <TableRow><TableCell>Cycle Credits</TableCell><TableCell>${Number(account.curr_cyc_credit).toFixed(2)}</TableCell></TableRow>
                <TableRow><TableCell>Cycle Debits</TableCell><TableCell>${Number(account.curr_cyc_debit).toFixed(2)}</TableCell></TableRow>
                <TableRow><TableCell>ZIP Code</TableCell><TableCell>{account.addr_zip}</TableCell></TableRow>
                <TableRow><TableCell>Group ID</TableCell><TableCell>{account.group_id}</TableCell></TableRow>
              </TableBody>
            </Table>
            <Box sx={{ mt: 2, display: "flex", gap: 1 }}>
              <Button variant="contained" onClick={() => navigate(`/accounts/${id}/edit`)}>Edit</Button>
              <Button variant="outlined" onClick={() => navigate("/accounts")}>Back to List</Button>
            </Box>
          </CardContent>
        </Card>
      )}
    </Box>
  );
}
