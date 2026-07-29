import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Box,
  Paper,
  Typography,
  Button,
  CircularProgress,
  Stack,
} from "@mui/material";
import ArrowBackIcon from "@mui/icons-material/ArrowBack";
import EditIcon from "@mui/icons-material/Edit";
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

const money = (v: string) => `$${Number(v).toFixed(2)}`;

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

  const rows: [string, string][] = account
    ? [
        ["Account ID", String(account.acct_id)],
        ["Status", account.active_status === "Y" ? "Active" : "Inactive"],
        ["Current Balance", money(account.curr_bal)],
        ["Credit Limit", money(account.credit_limit)],
        ["Cash Credit Limit", money(account.cash_credit_limit)],
        ["Open Date", account.open_date || "N/A"],
        ["Expiration Date", account.expiration_date || "N/A"],
        ["Reissue Date", account.reissue_date || "N/A"],
        ["Cycle Credits", money(account.curr_cyc_credit)],
        ["Cycle Debits", money(account.curr_cyc_debit)],
        ["ZIP Code", account.addr_zip || "N/A"],
        ["Group ID", account.group_id || "N/A"],
      ]
    : [];

  return (
    <Box sx={{ p: 3, maxWidth: 900, mx: "auto" }}>
      <ErrorMessage message={error} onClose={() => setError(null)} />
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
        <Button
          startIcon={<ArrowBackIcon />}
          onClick={() => navigate("/accounts")}
        >
          Back
        </Button>
        <Typography
          variant="h5"
          sx={{ fontWeight: 700, color: "primary.main" }}
        >
          Account Details
        </Typography>
        <Button
          variant="contained"
          startIcon={<EditIcon />}
          onClick={() => navigate(`/accounts/${id}/edit`)}
        >
          Edit
        </Button>
      </Stack>
      {account && (
        <Paper elevation={1} sx={{ borderRadius: 2, overflow: "hidden" }}>
          <Box
            sx={{
              display: "grid",
              gridTemplateColumns: { xs: "1fr", sm: "1fr 1fr" },
            }}
          >
            {rows.map(([label, value], i) => (
              <Box
                key={label}
                sx={{
                  display: "flex",
                  justifyContent: "space-between",
                  gap: 2,
                  px: 2,
                  py: 1.25,
                  bgcolor: i % 2 === 0 ? "grey.50" : "background.paper",
                }}
              >
                <Typography variant="body2" color="text.secondary" fontWeight={600}>
                  {label}
                </Typography>
                <Typography variant="body2" sx={{ textAlign: "right" }}>
                  {value}
                </Typography>
              </Box>
            ))}
          </Box>
        </Paper>
      )}
    </Box>
  );
}
