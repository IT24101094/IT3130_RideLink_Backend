const express = require('express');
const dotenv = require('dotenv');
const mongoose = require('mongoose');

dotenv.config();
const app = express();
app.use(express.json());

const PORT = process.env.PORT || 5001;

mongoose.connect(process.env.MONGO_URI)
    .then(() => console.log('Connected to MongoDB Atlas'))
    .catch((err) => console.error('MongoDB connection error:', err));

app.get('/api/accounts/health', (req, res) => {
    res.status(200).json({ status: 'Account Service is running' });
});

app.listen(PORT, () => {
    console.log(`Account Service running on port ${PORT}`);
});