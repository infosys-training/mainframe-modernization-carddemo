import { useEffect, useState } from "react";
import {
  Box,
  Card,
  CardContent,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableRow,
  CircularProgress,
  Accordion,
  AccordionSummary,
  AccordionDetails,
} from "@mui/material";
import { ExpandMore } from "@mui/icons-material";
import { getBillingStatements } from "../services/api";

interface Statement {
  acct_id: number;
  customer_name: string;
  statement_date: string;
  current_balance: string;
  credit_limit: string;
  available_credit: string;
  cycle_credits: string;
  cycle_debits: string;
  transactions: Array<{
    tran_id: string;
    tran_desc: string;
    tran_amt: string;
    orig_ts: string | null;
  }>;
}

export default function Billing() {
  const [statements, setStatements] = useState<Statement[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getBillingStatements()
      .then((res) => setStatements(res.data))
      .finally(() => setLoading(false));
  }, []);

  if (loading) return <Box sx={{ p: 3, textAlign: "center" }}><CircularProgress /></Box>;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>Billing Statements</Typography>
      {statements.map((stmt) => (
        <Accordion key={stmt.acct_id}>
          <AccordionSummary expandIcon={<ExpandMore />}>
            <Box sx={{ display: "flex", justifyContent: "space-between", width: "100%", pr: 2 }}>
              <Typography>Account {stmt.acct_id} - {stmt.customer_name}</Typography>
              <Typography>Balance: ${Number(stmt.current_balance).toFixed(2)}</Typography>
            </Box>
          </AccordionSummary>
          <AccordionDetails>
            <Card variant="outlined">
              <CardContent>
                <Typography variant="body2">Statement Date: {stmt.statement_date}</Typography>
                <Typography variant="body2">Credit Limit: ${Number(stmt.credit_limit).toFixed(2)}</Typography>
                <Typography variant="body2">Available Credit: ${Number(stmt.available_credit).toFixed(2)}</Typography>
                <Typography variant="body2">Cycle Credits: ${Number(stmt.cycle_credits).toFixed(2)}</Typography>
                <Typography variant="body2">Cycle Debits: ${Number(stmt.cycle_debits).toFixed(2)}</Typography>
                {stmt.transactions.length > 0 && (
                  <Table size="small" sx={{ mt: 2 }}>
                    <TableHead>
                      <TableRow>
                        <TableCell>ID</TableCell>
                        <TableCell>Description</TableCell>
                        <TableCell align="right">Amount</TableCell>
                        <TableCell>Date</TableCell>
                      </TableRow>
                    </TableHead>
                    <TableBody>
                      {stmt.transactions.map((txn) => (
                        <TableRow key={txn.tran_id}>
                          <TableCell>{txn.tran_id}</TableCell>
                          <TableCell>{txn.tran_desc}</TableCell>
                          <TableCell align="right">${Number(txn.tran_amt).toFixed(2)}</TableCell>
                          <TableCell>{txn.orig_ts ? new Date(txn.orig_ts).toLocaleDateString() : "N/A"}</TableCell>
                        </TableRow>
                      ))}
                    </TableBody>
                  </Table>
                )}
              </CardContent>
            </Card>
          </AccordionDetails>
        </Accordion>
      ))}
    </Box>
  );
}
