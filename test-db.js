const { pool } = require('../lib/db');

async function testConnection() {
  try {
    console.log('Test de connexion à la base de données...');
    const [rows] = await pool.execute('SELECT 1 as test');
    console.log('✅ Connexion réussie:', rows);

    // Tester la récupération d'un utilisateur de test
    const [users] = await pool.execute('SELECT id, email, full_name, role FROM users LIMIT 1');
    console.log('✅ Utilisateur trouvé:', users);

    process.exit(0);
  } catch (error) {
    console.error('❌ Erreur de connexion:', error);
    process.exit(1);
  }
}

testConnection();