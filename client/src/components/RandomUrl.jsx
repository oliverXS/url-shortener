import TextField from '@mui/material/TextField';
import Button from '@mui/material/Button';
import Stack from '@mui/material/Stack';
import Typography from '@mui/material/Typography';
import Box from '@mui/material/Box';
import api from '../api';
import { useState } from 'react';
import axios from 'axios';

const RandomUrl = () => {
    const [longUrl, setLongUrl] = useState("");
    const [shortUrl, setShortUrl] = useState("");
    const [qrCodeImage, setQrCodeImage] = useState(null);
    
    const handleShortenUrl = async () => {
        console.log('handleShortenUrl called');
        try {
            console.log('About to call api.post');
            const response = await api.post("/shorten",  { longUrl: longUrl });
            setShortUrl(response.data);
            console.log('Called api.post, response:', response);
            // Generate QR code
            if (response.data) {
                const res = await api.get(`/api/generateQR/${response.data}`, { responseType: 'blob' });
                console.log(`Received blob of size ${res.data.size} and type ${res.data.type}`);
                const url = URL.createObjectURL(res.data);
                console.log(`Created object URL: ${url}`);
                setQrCodeImage(url);
            }
        } catch (error) {
            console.error(error);
        }
    };


    return (
        <Stack spacing={2} width="60%">
            <TextField
                variant="outlined"
                label="Long URL"
                value={longUrl}
                onChange={(e) => setLongUrl(e.target.value)}
                placeholder="Enter a long URL"
                margin="normal"
                fullWidth
            ></TextField>
            <Button variant="outlined" color="primary" onClick={handleShortenUrl} fullWidth>
                Generate Random URL
            </Button>
            <Box display="flex" justifyContent="center">
            {shortUrl && (
                <Typography variant="h5" component="h2" gutterBottom>
                    Short URL: {shortUrl}
                </Typography>
            )}
            </Box>
            <Box display="flex" justifyContent="center">
            {qrCodeImage && (
                <img src={qrCodeImage} alt="QR Code" />
            )}
            </Box>
        </Stack>
    )
}

export default RandomUrl
