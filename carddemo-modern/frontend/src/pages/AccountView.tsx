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
import EditIcon from "@mui/icons-material/Edit";
import { getAccount } from "../services/api";
import ErrorMessage from "../components/ErrorMessage";
import { formatDate } from "../utils/date";

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

const ROW_H = 44;

function StripedRows({ rows }: { rows: [string, string][] }) {
  return (
    <Box>
      {rows.map(([label, value], i) => (
        <Box
          key={label}
          sx={{
            display: "flex",
            justifyContent: "space-between",
            alignItems: "center",
            gap: 2,
            px: 2,
            height: ROW_H,
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
  );
}

export default function AccountView() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [account, setAccount] = useState<Account | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [flipped, setFlipped] = useState(false);

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

  const primary: [string, string][] = account
    ? [
        ["Account ID", String(account.acct_id)],
        ["Status", account.active_status === "Y" ? "Active" : "Inactive"],
        ["Current Balance", money(account.curr_bal)],
        ["Credit Limit", money(account.credit_limit)],
        ["Cash Credit Limit", money(account.cash_credit_limit)],
      ]
    : [];

  const details: [string, string][] = account
    ? [
        ["Open Date", formatDate(account.open_date)],
        ["Expiration Date", formatDate(account.expiration_date)],
        ["Reissue Date", formatDate(account.reissue_date)],
        ["Cycle Credits", money(account.curr_cyc_credit)],
        ["Cycle Debits", money(account.curr_cyc_debit)],
        ["ZIP Code", account.addr_zip || "N/A"],
        ["Group ID", account.group_id || "N/A"],
      ]
    : [];

  // Card height sized to the taller (back) face so the flip doesn't clip.
  const cardHeight = details.length * ROW_H + 56;

  return (
    <Box sx={{ p: 3, maxWidth: 720, mx: "auto" }}>
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
        <Button onClick={() => navigate("/accounts")}>&lt;&nbsp;&nbsp;Back</Button>
        <Typography variant="h5" sx={{ fontWeight: 700, color: "primary.main" }}>
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
        <Box sx={{ perspective: "1800px" }}>
          <Box
            sx={{
              position: "relative",
              height: cardHeight,
              transformStyle: "preserve-3d",
              transition: "transform 0.6s",
              transform: flipped ? "rotateY(180deg)" : "none",
            }}
          >
            {/* Front — key details */}
            <Paper
              elevation={1}
              sx={{
                position: "absolute",
                inset: 0,
                borderRadius: 2,
                overflow: "hidden",
                backfaceVisibility: "hidden",
                display: "flex",
                flexDirection: "column",
              }}
            >
              <StripedRows rows={primary} />
              <Box sx={{ mt: "auto", p: 1.5, display: "flex", justifyContent: "center" }}>
                <Button size="small" onClick={() => setFlipped(true)}>
                  More details
                </Button>
              </Box>
            </Paper>

            {/* Back — all remaining details */}
            <Paper
              elevation={1}
              sx={{
                position: "absolute",
                inset: 0,
                borderRadius: 2,
                overflow: "hidden",
                backfaceVisibility: "hidden",
                transform: "rotateY(180deg)",
                display: "flex",
                flexDirection: "column",
              }}
            >
              <StripedRows rows={details} />
              <Box sx={{ mt: "auto", p: 1.5, display: "flex", justifyContent: "center" }}>
                <Button size="small" onClick={() => setFlipped(false)}>
                  Less details
                </Button>
              </Box>
            </Paper>
          </Box>
        </Box>
      )}
    </Box>
  );
}
