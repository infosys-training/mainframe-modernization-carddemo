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
import { getTransactions } from "../services/api";
import PaginationControls from "../components/PaginationControls";

interface Txn {
  tran_id: string;
  tran_type_cd: string;
  tran_desc: string;
  tran_amt: string;
  card_num: string;
  orig_ts: string | null;
}

export default function TransactionList() {
  const [transactions, setTransactions] = useState<Txn[]>([]);
  const [page, setPage] = useState(1);
  const navigate = useNavigate();
  const pageSize = 20;

  useEffect(() => {
    getTransactions(page, pageSize).then((res) => setTransactions(res.data));
  }, [page]);

  return (
    <Box sx={{ p: 3 }}>
      <Box sx={{ display: "flex", justifyContent: "space-between", alignItems: "center", mb: 2 }}>
        <Typography variant="h5">Transactions</Typography>
        <Button variant="contained" onClick={() => navigate("/transactions/add")}>Add Transaction</Button>
      </Box>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell>Transaction ID</TableCell>
              <TableCell>Type</TableCell>
              <TableCell>Description</TableCell>
              <TableCell align="right">Amount</TableCell>
              <TableCell>Card</TableCell>
              <TableCell>Date</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {transactions.map((txn) => (
              <TableRow key={txn.tran_id} hover sx={{ cursor: "pointer" }} onClick={() => navigate(`/transactions/${txn.tran_id}`)}>
                <TableCell>{txn.tran_id}</TableCell>
                <TableCell>{txn.tran_type_cd}</TableCell>
                <TableCell>{txn.tran_desc}</TableCell>
                <TableCell align="right">${Number(txn.tran_amt).toFixed(2)}</TableCell>
                <TableCell>{txn.card_num}</TableCell>
                <TableCell>{txn.orig_ts ? new Date(txn.orig_ts).toLocaleDateString() : "N/A"}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <PaginationControls
        page={page}
        hasMore={transactions.length === pageSize}
        onPrev={() => setPage((p) => Math.max(1, p - 1))}
        onNext={() => setPage((p) => p + 1)}
      />
    </Box>
  );
}
