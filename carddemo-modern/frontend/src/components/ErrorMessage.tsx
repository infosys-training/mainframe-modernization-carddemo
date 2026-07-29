import { Alert, Snackbar } from "@mui/material";

interface Props {
  message: string | null;
  onClose: () => void;
}

export default function ErrorMessage({ message, onClose }: Props) {
  if (!message) return null;
  return (
    <Snackbar open={!!message} autoHideDuration={6000} onClose={onClose}>
      <Alert onClose={onClose} severity="error" sx={{ width: "100%" }}>
        {message}
      </Alert>
    </Snackbar>
  );
}
