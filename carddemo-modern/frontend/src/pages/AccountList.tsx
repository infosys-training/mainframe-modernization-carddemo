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
  Stack,
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

  const headCellSx = {
    fontWeight: 700,
    color: "grey.900",
    bgcolor: "grey.200",
    borderBottom: "2px solid",
    borderBottomColor: "grey.400",
  };

  return (
    <Box sx={{ p: 3 }}>
      <Stack
        direction="row"
        alignItems="center"
        justifyContent="space-between"
        sx={{
          mb: 2,
          pb: 1.5,
          borderBottom: "3px solid",
          borderBottomColor: "primary.main",
        }}
      >
        <Typography
          variant="h4"
          sx={{ fontWeight: 700, letterSpacing: "-0.5px", color: "primary.main" }}
        >
          Accounts
        </Typography>
        <Button
          variant="contained"
          onClick={() => navigate("/accounts/new")}
        >
          + Account
        </Button>
      </Stack>
      <TableContainer component={Paper}>
        <Table size="small">
          <TableHead>
            <TableRow>
              <TableCell sx={headCellSx}>Account ID</TableCell>
              <TableCell sx={headCellSx}>Status</TableCell>
              <TableCell sx={headCellSx} align="right">Balance</TableCell>
              <TableCell sx={headCellSx} align="right">Credit Limit</TableCell>
              <TableCell sx={headCellSx}>Open Date</TableCell>
              <TableCell sx={headCellSx}>Actions</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {accounts.map((acct) => (
              <TableRow
                key={acct.acct_id}
                hover
                sx={{ "&:nth-of-type(odd)": { bgcolor: "grey.50" } }}
              >
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
