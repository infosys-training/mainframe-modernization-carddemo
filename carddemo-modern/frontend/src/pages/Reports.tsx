import { useState } from "react";
import {
  Box,
  Button,
  TextField,
  Typography,
  Table,
  TableBody,
  TableCell,
  TableContainer,
  TableHead,
  TableRow,
  Paper,
  Alert,
} from "@mui/material";
import { getTransactionReport } from "../services/api";

interface ReportItem {
  tran_id: string;
  acct_id: number | null;
  tran_type_cd: string;
  tran_type_desc: string;
  tran_cat_cd: number;
  tran_cat_desc: string;
  tran_source: string;
  tran_amt: string;
  orig_ts: string | null;
}

interface Report {
  report_name: string;
  start_date: string;
  end_date: string;
  items: ReportItem[];
  total_amount: string;
  record_count: number;
}

export default function Reports() {
  const [startDate, setStartDate] = useState("");
  const [endDate, setEndDate] = useState("");
  const [report, setReport] = useState<Report | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  const handleGenerate = async () => {
    setLoading(true);
    setError("");
    try {
      const params: Record<string, string> = {};
      if (startDate) params.start_date = startDate;
      if (endDate) params.end_date = endDate;
      const res = await getTransactionReport(params);
      setReport(res.data);
    } catch {
      setError("Failed to generate report");
    } finally {
      setLoading(false);
    }
  };

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>Transaction Reports</Typography>
      {error && <Alert severity="error" sx={{ mb: 2 }}>{error}</Alert>}
      <Box sx={{ display: "flex", gap: 2, mb: 3, flexWrap: "wrap" }}>
        <TextField label="Start Date" type="date" value={startDate} onChange={(e) => setStartDate(e.target.value)} InputLabelProps={{ shrink: true }} />
        <TextField label="End Date" type="date" value={endDate} onChange={(e) => setEndDate(e.target.value)} InputLabelProps={{ shrink: true }} />
        <Button variant="contained" onClick={handleGenerate} disabled={loading}>
          {loading ? "Generating..." : "Generate Report"}
        </Button>
      </Box>
      {report && (
        <>
          <Typography variant="h6">{report.report_name}</Typography>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            {new Date(report.start_date + "T00:00").toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric" })} to {new Date(report.end_date + "T00:00").toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric" })} | {report.record_count} records | Total: ${Number(report.total_amount).toFixed(2)}
          </Typography>
          <TableContainer component={Paper}>
            <Table size="small">
              <TableHead>
                <TableRow>
                  <TableCell>Transaction ID</TableCell>
                  <TableCell>Account</TableCell>
                  <TableCell>Type</TableCell>
                  <TableCell>Category</TableCell>
                  <TableCell>Source</TableCell>
                  <TableCell align="right">Amount</TableCell>
                  <TableCell>Date</TableCell>
                </TableRow>
              </TableHead>
              <TableBody>
                {report.items.map((item) => (
                  <TableRow key={item.tran_id}>
                    <TableCell>{item.tran_id}</TableCell>
                    <TableCell>{item.acct_id ?? "N/A"}</TableCell>
                    <TableCell>{item.tran_type_cd}-{item.tran_type_desc}</TableCell>
                    <TableCell>{item.tran_cat_cd}-{item.tran_cat_desc}</TableCell>
                    <TableCell>{item.tran_source}</TableCell>
                    <TableCell align="right">${Number(item.tran_amt).toFixed(2)}</TableCell>
                    <TableCell>{item.orig_ts ? new Date(item.orig_ts).toLocaleDateString("en-US", { year: "numeric", month: "short", day: "numeric" }) : "N/A"}</TableCell>
                  </TableRow>
                ))}
              </TableBody>
            </Table>
          </TableContainer>
        </>
      )}
    </Box>
  );
}
