import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import api from '../api';
import { useState } from 'react';

const CustomizeUrl = () => {
    const [longUrl, setLongUrl] = useState("");
    const [shortUrl, setShortUrl] = useState("");
    const [customizedUrl, setCustomizedUrl] = useState("");

    const handleCustomizeUrl = async () => {
        try {
          const response = await api.post('/customize', {
            longUrl,
            customizedUrl,
          });
          setShortUrl(response.data);
        } catch (error) {
          console.error(error);
        }
      };

    return (
        <Stack spacing={2} width="60%">
            <TextField
                variant="outlined"
                label="Long URL"
                placeholder="Enter a long URL"
                fullWidth
                margin="normal"
            ></TextField>
            <TextField
                variant="outlined"
                label="Customized URL"
                placeholder="Enter a Customized URL"
                fullWidth
                margin="normal"
            ></TextField>
            <Button variant="outlined" color="primary">
                Generate Customized URL
            </Button>
            {shortUrl && (
                <Typography variant="h5" component="h2" gutterBottom>
                    Short URL: {shortUrl}
                </Typography>
            )}
        </Stack>
    )
}

export default CustomizeUrl
