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
  Pagination,
  TextField,
  MenuItem,
} from "@mui/material";
import { getAccounts } from "../services/api";
import { formatDate } from "../utils/date";

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
  const [pageSize, setPageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const navigate = useNavigate();
  const totalPages = Math.max(1, Math.ceil(total / pageSize));

  useEffect(() => {
    getAccounts(page, pageSize).then((res) => {
      setAccounts(res.data);
      const count = Number(res.headers["x-total-count"]);
      if (!Number.isNaN(count)) setTotal(count);
    });
  }, [page, pageSize]);

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
        sx={{
          alignItems: "center",
          justifyContent: "space-between",
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
                <TableCell>{formatDate(acct.open_date)}</TableCell>
                <TableCell>
                  <Button size="small" onClick={() => navigate(`/accounts/${acct.acct_id}`)}>View</Button>
                  <Button size="small" onClick={() => navigate(`/accounts/${acct.acct_id}/edit`)}>Edit</Button>
                </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </TableContainer>
      <Box
        sx={{
          mt: 2,
          display: "flex",
          flexWrap: "wrap",
          gap: 1,
          alignItems: "center",
          justifyContent: "space-between",
        }}
      >
        <TextField
          select
          size="small"
          label="Rows per page"
          value={pageSize}
          onChange={(e) => {
            setPageSize(Number(e.target.value));
            setPage(1);
          }}
          sx={{ width: 150 }}
        >
          {[10, 15, 20, 50, 100].map((n) => (
            <MenuItem key={n} value={n}>{n}</MenuItem>
          ))}
        </TextField>
        <Pagination
          count={totalPages}
          page={page}
          onChange={(_, p) => setPage(p)}
          color="primary"
          shape="rounded"
          showFirstButton
          showLastButton
          siblingCount={1}
          boundaryCount={1}
        />
      </Box>
    </Box>
  );
}
