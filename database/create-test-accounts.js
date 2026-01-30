#!/usr/bin/env node

const { Pool } = require('pg');
const bcrypt = require('bcryptjs');

async function createTestAccounts() {
  // Configuration de la base de données
  const pool = new Pool({
    connectionString: process.env.DATABASE_URL || 'postgresql://user:password@localhost:5432/campusmaster',
    ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false,
  });

  try {
    console.log('🔄 Connexion à la base de données...');

    // Vérifier si la table profiles existe
    const tableCheck = await pool.query(`
      SELECT EXISTS (
        SELECT FROM information_schema.tables
        WHERE table_name = 'profiles'
      );
    `);

    if (!tableCheck.rows[0].exists) {
      console.log('❌ La table profiles n\'existe pas. Veuillez d\'abord exécuter le script d\'initialisation complète.');
      console.log('Exécutez : npm run db:init');
      return;
    }

    console.log('✅ Table profiles trouvée. Insertion des comptes de test...');

    // Hash des mots de passe
    const adminPassword = await bcrypt.hash('Admin123!', 10);
    const teacherPassword = await bcrypt.hash('Prof123!', 10);
    const studentPassword = await bcrypt.hash('Student123!', 10);

    // Insérer les comptes de test
    const result = await pool.query(`
      INSERT INTO profiles (email, password_hash, full_name, role) VALUES
      ('admin@campusmaster.com', $1, 'Administrateur CampusMaster', 'admin'),
      ('enseignant@campusmaster.com', $2, 'Professeur Dupont', 'teacher'),
      ('etudiant@campusmaster.com', $3, 'Étudiant Martin', 'student')
      ON CONFLICT (email) DO UPDATE SET
        password_hash = EXCLUDED.password_hash,
        full_name = EXCLUDED.full_name,
        role = EXCLUDED.role;
    `, [adminPassword, teacherPassword, studentPassword]);

    console.log('✅ Comptes de test créés/mis à jour avec succès !');
    console.log('');
    console.log('📋 Comptes de test disponibles pour tester les interfaces :');
    console.log('');
    console.log('👑 ADMINISTRATEUR :');
    console.log('   📧 Email : admin@campusmaster.com');
    console.log('   🔒 Mot de passe : Admin123!');
    console.log('   🎯 Interface : Gestion complète du système');
    console.log('');
    console.log('👨‍🏫 ENSEIGNANT :');
    console.log('   📧 Email : enseignant@campusmaster.com');
    console.log('   🔒 Mot de passe : Prof123!');
    console.log('   🎯 Interface : Gestion des cours et devoirs');
    console.log('');
    console.log('🎓 ÉTUDIANT :');
    console.log('   📧 Email : etudiant@campusmaster.com');
    console.log('   🔒 Mot de passe : Student123!');
    console.log('   🎯 Interface : Accès aux cours et soumissions');
    console.log('');
    console.log('🚀 Vous pouvez maintenant vous connecter avec ces comptes pour explorer les différentes interfaces !');

  } catch (error) {
    console.error('❌ Erreur lors de la création des comptes:', error.message);
    console.log('');
    console.log('💡 Solutions possibles :');
    console.log('1. Vérifiez que DATABASE_URL est correctement configuré dans .env');
    console.log('2. Assurez-vous que la base de données PostgreSQL est accessible');
    console.log('3. Si c\'est la première fois, exécutez d\'abord : npm run db:init');
  } finally {
    await pool.end();
  }
}

createTestAccounts();