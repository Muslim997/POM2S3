#!/usr/bin/env node

const fs = require('fs');
const path = require('path');
require('dotenv').config({ path: path.join(__dirname, '..', '.env') });
const { createClient } = require('@supabase/supabase-js');
const bcrypt = require('bcryptjs');

async function initializeDatabase() {
  const supabase = createClient(
    process.env.NEXT_PUBLIC_SUPABASE_URL,
    process.env.NEXT_PUBLIC_SUPABASE_ANON_KEY
  );

  try {
    console.log('Connexion à Supabase...');

    // Vérifier la connexion
    const { data, error: connectionError } = await supabase.from('profiles').select('count').limit(1);
    if (connectionError) {
      console.error('Erreur de connexion à Supabase:', connectionError);
      return;
    }

    console.log('Insertion des comptes de test...');

    const adminPassword = await bcrypt.hash('admin123', 10);
    const teacherPassword = await bcrypt.hash('teacher123', 10);
    const studentPassword = await bcrypt.hash('student123', 10);

    // Insérer les comptes de test
    const accounts = [
      {
        email: 'admin@campus.com',
        password_hash: adminPassword,
        full_name: 'Administrateur CampusMaster',
        role: 'admin'
      },
      {
        email: 'teacher@campus.com',
        password_hash: teacherPassword,
        full_name: 'Professeur Dupont',
        role: 'teacher'
      },
      {
        email: 'student@campus.com',
        password_hash: studentPassword,
        full_name: 'Étudiant Martin',
        role: 'student'
      }
    ];

    for (const account of accounts) {
      const { error: insertError } = await supabase
        .from('profiles')
        .upsert(account, { onConflict: 'email' });

      if (insertError) {
        console.error(`Erreur lors de l'insertion de ${account.email}:`, insertError);
      } else {
        console.log(`Compte ${account.email} inséré ou mis à jour.`);
      }
    }

    console.log('Comptes de test créés avec succès !');
    console.log('');
    console.log('📋 Comptes de test disponibles :');
    console.log('');
    console.log('👑 ADMINISTRATEUR :');
    console.log('   Email : admin@campus.com');
    console.log('   Mot de passe : admin123');
    console.log('');
    console.log('👨‍🏫 ENSEIGNANT :');
    console.log('   Email : teacher@campus.com');
    console.log('   Mot de passe : teacher123');
    console.log('');
    console.log('🎓 ÉTUDIANT :');
    console.log('   Email : student@campus.com');
    console.log('   Mot de passe : student123');
    console.log('');
    console.log('✅ Vous pouvez maintenant vous connecter avec ces comptes pour tester l\'interface de chaque rôle.');

  } catch (error) {
    console.error('Erreur lors de l\'initialisation:', error);
  }
}

initializeDatabase();