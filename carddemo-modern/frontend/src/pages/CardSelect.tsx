import { useEffect, useState } from "react";
import { useParams, useNavigate } from "react-router-dom";
import {
  Box,
  Card,
  CardContent,
  Typography,
  Button,
  Table,
  TableBody,
  TableRow,
  TableCell,
  CircularProgress,
} from "@mui/material";
import { getCard } from "../services/api";

interface CardData {
  card_num: string;
  acct_id: number;
  cvv_cd: string;
  embossed_name: string;
  expiration_date: string | null;
  active_status: string;
}

export default function CardSelect() {
  const { cardNum } = useParams<{ cardNum: string }>();
  const navigate = useNavigate();
  const [card, setCard] = useState<CardData | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (!cardNum) return;
    getCard(cardNum)
      .then((res) => setCard(res.data))
      .finally(() => setLoading(false));
  }, [cardNum]);

  if (loading) return <Box sx={{ p: 3, textAlign: "center" }}><CircularProgress /></Box>;

  return (
    <Box sx={{ p: 3 }}>
      <Typography variant="h5" gutterBottom>Card Details</Typography>
      {card && (
        <Card>
          <CardContent>
            <Table>
              <TableBody>
                <TableRow><TableCell>Card Number</TableCell><TableCell>{card.card_num}</TableCell></TableRow>
                <TableRow><TableCell>Account ID</TableCell><TableCell>{card.acct_id}</TableCell></TableRow>
                <TableRow><TableCell>CVV</TableCell><TableCell>***</TableCell></TableRow>
                <TableRow><TableCell>Embossed Name</TableCell><TableCell>{card.embossed_name}</TableCell></TableRow>
                <TableRow><TableCell>Expiration Date</TableCell><TableCell>{card.expiration_date || "N/A"}</TableCell></TableRow>
                <TableRow><TableCell>Status</TableCell><TableCell>{card.active_status === "Y" ? "Active" : "Inactive"}</TableCell></TableRow>
              </TableBody>
            </Table>
            <Box sx={{ mt: 2, display: "flex", gap: 1 }}>
              <Button variant="contained" onClick={() => navigate(`/cards/${cardNum}/edit`)}>Edit</Button>
              <Button variant="outlined" onClick={() => navigate("/cards")}>Back to List</Button>
            </Box>
          </CardContent>
        </Card>
      )}
    </Box>
  );
}
