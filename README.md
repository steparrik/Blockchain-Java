# Blockchain in Java

This project is a simple blockchain implementation inspired by the principles of Bitcoin. It uses a **Proof of Work (PoW)** consensus mechanism and stores transactions in the **UTXO model**. The application provides:

- A **blockchain API** to interact with the blockchain.
- A **console-based client** for creating accounts, signing transactions, and sending them.

The stack includes **Java**, **Spring Boot**, and **LevelDB** for efficient data storage.

---

## Features

### Blockchain API
- Add new transactions and mine blocks.
- Verify transactions using a **witness mechanism**.
- Retrieve blockchain and UTXO data.

### Client Console Application
- Create accounts with key pair management.
- Sign and submit transactions to the blockchain.
- View account balances and blockchain data.

### Core Features
- **Consensus**: Implements the **Proof of Work (PoW)** algorithm.
- **Data Storage**:
  - **LevelDB** for lightweight and fast storage of blockchain and UTXO data.
  - Local LevelDB for temporary storage of account data on the client.

---

## How It Works

1. **Account Management**: Users create accounts with public-private key pairs. Account data is stored locally on the client.
2. **Transaction Processing**: Transactions are signed by senders and verified using witnesses.
3. **Proof of Work**: Miners solve cryptographic puzzles to validate blocks.
4. **Data Persistence**: Blockchain and UTXO data are stored persistently in LevelDB.

---

## Tech Stack

- **Java** with **Spring Boot** for the backend.
- **LevelDB** for key-value data storage.
