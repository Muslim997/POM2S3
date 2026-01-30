const { createClient } = require('@supabase/supabase-js');
const bcrypt = require('bcryptjs');

const supabase = createClient(
  'https://campusmaster-campusmaster-v1.onrender.com',
  'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InRncmpmcXRmaGNmbHhjYXNrcWV5Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NjQ1NTg2NjUsImV4cCI6MjA4MDEzNDY2NX0.Vklp_eHZkLV3b70sswcwKCbzdHp1w15l3BrO88rCfX8'
);

async function createTestAccounts() {
  try {
    console.log('Création des comptes de test...');

    // Compte Admin
    const adminPassword = await bcrypt.hash('admin123', 10);
    const { error: adminError } = await supabase.from('users').upsert({
      email: 'admin@campus.com',
      password_hash: adminPassword,
      full_name: 'Administrateur',
      role: 'admin'
    });

    if (adminError) {
      console.error('Erreur admin:', adminError);
    } else {
      console.log('✅ Compte admin créé');
    }

    // Compte Enseignant
    const teacherPassword = await bcrypt.hash('teacher123', 10);
    const { error: teacherError } = await supabase.from('users').upsert({
      email: 'teacher@campus.com',
      password_hash: teacherPassword,
      full_name: 'Professeur Dupont',
      role: 'teacher'
    });

    if (teacherError) {
      console.error('Erreur teacher:', teacherError);
    } else {
      console.log('✅ Compte teacher créé');
    }

    // Compte Étudiant
    const studentPassword = await bcrypt.hash('student123', 10);
    const { error: studentError } = await supabase.from('users').upsert({
      email: 'student@campus.com',
      password_hash: studentPassword,
      full_name: 'Étudiant Martin',
      role: 'student'
    });

    if (studentError) {
      console.error('Erreur student:', studentError);
    } else {
      console.log('✅ Compte student créé');
    }

    console.log('Comptes de test créés avec succès !');
    console.log('Admin: admin@campus.com / admin123');
    console.log('Teacher: teacher@campus.com / teacher123');
    console.log('Student: student@campus.com / student123');

  } catch (error) {
    console.error('Erreur lors de la création des comptes:', error);
  }
}

createTestAccounts();