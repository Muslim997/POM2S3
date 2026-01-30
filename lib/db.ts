import { createClient } from '@supabase/supabase-js';

// Base de données en mémoire pour les tests avec données sénégalaises
const users = [
  {
    id: '1',
    email: 'admin@campus.com',
    password_hash: '$2b$10$oeC/z9M/zsqs0TGhosb5ieviBkrzWuifaG7wxlItZFyH5DlZIzxz2', // admin123
    full_name: 'Mamadou Diop',
    role: 'admin',
    avatar_url: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=150&h=150&fit=crop&crop=face',
    phone: '+221 77 123 45 67',
    bio: 'Administrateur principal de CampusMaster Sénégal. Ancien doyen de la faculté des sciences.',
    department_id: '1',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  },
  {
    id: '2',
    email: 'teacher@campus.com',
    password_hash: '$2b$10$8n/fvX6cKCzZRm31LTzT5.oMq25tl.tuEYccrvJmUYtQ4sz6Gpn3y', // teacher123
    full_name: 'Dr. Fatou Sow',
    role: 'teacher',
    avatar_url: 'https://images.unsplash.com/photo-1494790108755-2616b612b786?w=150&h=150&fit=crop&crop=face',
    phone: '+221 76 234 56 78',
    bio: 'Professeure agrégée en Informatique. Spécialiste en Intelligence Artificielle et Big Data.',
    department_id: '1',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  },
  {
    id: '3',
    email: 'student@campus.com',
    password_hash: '$2b$10$EsIa5ChVIBm5IoxpL.U5.e17Yw18CQUcqghZlHJuenFUj5vwfmv4a', // student123
    full_name: 'Aminata Ndiaye',
    role: 'student',
    avatar_url: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=150&h=150&fit=crop&crop=face',
    phone: '+221 78 345 67 89',
    bio: 'Étudiante en Master 2 Informatique. Passionnée par le développement web et mobile.',
    department_id: '1',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  },
  {
    id: '4',
    email: 'prof.math@campus.com',
    password_hash: '$2b$10$EsIa5ChVIBm5IoxpL.U5.e17Yw18CQUcqghZlHJuenFUj5vwfmv4a', // teacher123
    full_name: 'Pr. Ibrahima Faye',
    role: 'teacher',
    avatar_url: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=150&h=150&fit=crop&crop=face',
    phone: '+221 77 456 78 90',
    bio: 'Professeur titulaire en Mathématiques appliquées. Expert en statistiques et probabilités.',
    department_id: '2',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  },
  {
    id: '5',
    email: 'etudiant2@campus.com',
    password_hash: '$2b$10$EsIa5ChVIBm5IoxpL.U5.e17Yw18CQUcqghZlHJuenFUj5vwfmv4a', // student123
    full_name: 'Cheikh Tidiane Sy',
    role: 'student',
    avatar_url: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=150&h=150&fit=crop&crop=face',
    phone: '+221 76 567 89 01',
    bio: 'Étudiant en Master 2 Mathématiques. Intéressé par la recherche opérationnelle.',
    department_id: '2',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  },
  {
    id: '6',
    email: 'prof.physique@campus.com',
    password_hash: '$2b$10$EsIa5ChVIBm5IoxpL.U5.e17Yw18CQUcqghZlHJuenFUj5vwfmv4a', // teacher123
    full_name: 'Dr. Marie Louise Diagne',
    role: 'teacher',
    avatar_url: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?w=150&h=150&fit=crop&crop=face',
    phone: '+221 78 678 90 12',
    bio: 'Docteure en Physique. Spécialiste en physique quantique et nanotechnologies.',
    department_id: '3',
    created_at: new Date().toISOString(),
    updated_at: new Date().toISOString()
  }
];

const departments = [
  {
    id: '1',
    name: 'Informatique',
    code: 'INFO',
    description: 'Département d\'Informatique - UCAD Dakar',
    created_at: new Date().toISOString()
  },
  {
    id: '2',
    name: 'Mathématiques',
    code: 'MATH',
    description: 'Département de Mathématiques - UCAD Dakar',
    created_at: new Date().toISOString()
  },
  {
    id: '3',
    name: 'Physique',
    code: 'PHYS',
    description: 'Département de Physique - UCAD Dakar',
    created_at: new Date().toISOString()
  }
];

const semesters = [
  {
    id: '1',
    name: 'Semestre 1 - 2025',
    code: 'S1-2025',
    start_date: '2025-09-01',
    end_date: '2026-01-31',
    created_at: new Date().toISOString()
  },
  {
    id: '2',
    name: 'Semestre 2 - 2025',
    code: 'S2-2025',
    start_date: '2026-02-01',
    end_date: '2026-06-30',
    created_at: new Date().toISOString()
  }
];

const subjects = [
  {
    id: '1',
    title: 'Intelligence Artificielle Avancée',
    code: 'INFO-501',
    description: 'Cours avancé sur les techniques d\'intelligence artificielle, incluant le machine learning, le deep learning et les réseaux de neurones.',
    department_id: '1',
    semester_id: '1',
    teacher_id: '2',
    credits: 6,
    created_at: new Date().toISOString()
  },
  {
    id: '2',
    title: 'Développement Web Full-Stack',
    code: 'INFO-502',
    description: 'Développement d\'applications web modernes avec React, Node.js et les bases de données NoSQL.',
    department_id: '1',
    semester_id: '1',
    teacher_id: '2',
    credits: 5,
    created_at: new Date().toISOString()
  },
  {
    id: '3',
    title: 'Statistiques Avancées',
    code: 'MATH-501',
    description: 'Méthodes statistiques avancées pour l\'analyse de données et la recherche opérationnelle.',
    department_id: '2',
    semester_id: '1',
    teacher_id: '4',
    credits: 4,
    created_at: new Date().toISOString()
  },
  {
    id: '4',
    title: 'Physique Quantique',
    code: 'PHYS-501',
    description: 'Introduction aux principes de la mécanique quantique et ses applications modernes.',
    department_id: '3',
    semester_id: '1',
    teacher_id: '6',
    credits: 5,
    created_at: new Date().toISOString()
  }
];

const assignments = [
  {
    id: '1',
    subject_id: '1',
    title: 'Projet IA - Classification d\'images',
    description: 'Développer un modèle de classification d\'images utilisant TensorFlow pour reconnaître les fruits tropicaux sénégalais.',
    instructions: 'Utilisez le dataset des fruits tropicaux. Implémentez un CNN avec au moins 3 couches convolutives. Évaluez les performances avec précision, rappel et F1-score.',
    due_date: '2025-11-15T23:59:59Z',
    max_points: 20,
    created_by: '2',
    created_at: new Date().toISOString()
  },
  {
    id: '2',
    subject_id: '2',
    title: 'Application e-commerce sénégalaise',
    description: 'Créer une plateforme e-commerce pour les artisans sénégalais utilisant Next.js et Supabase.',
    instructions: 'L\'application doit inclure : authentification, catalogue produits, panier, paiement (simulation), et interface admin.',
    due_date: '2025-12-01T23:59:59Z',
    max_points: 25,
    created_by: '2',
    created_at: new Date().toISOString()
  },
  {
    id: '3',
    subject_id: '3',
    title: 'Analyse statistique des données démographiques',
    description: 'Analyser les données démographiques du Sénégal en utilisant R ou Python.',
    instructions: 'Utilisez les données de l\'ANSD. Effectuez une analyse descriptive, des tests statistiques et créez des visualisations.',
    due_date: '2025-11-20T23:59:59Z',
    max_points: 18,
    created_by: '4',
    created_at: new Date().toISOString()
  }
];

const enrollments = [
  { id: '1', student_id: '3', subject_id: '1', enrolled_at: new Date().toISOString() },
  { id: '2', student_id: '3', subject_id: '2', enrolled_at: new Date().toISOString() },
  { id: '3', student_id: '5', subject_id: '3', enrolled_at: new Date().toISOString() },
  { id: '4', student_id: '3', subject_id: '4', enrolled_at: new Date().toISOString() }
];

const submissions = [
  {
    id: '1',
    assignment_id: '1',
    student_id: '3',
    content: 'Mon projet de classification d\'images utilisant CNN pour les fruits tropicaux sénégalais.',
    submitted_at: '2025-11-10T14:30:00Z',
    status: 'submitted',
    version: 1
  },
  {
    id: '2',
    assignment_id: '2',
    student_id: '3',
    content: 'Application e-commerce pour les artisans sénégalais avec Next.js.',
    submitted_at: '2025-11-25T16:45:00Z',
    status: 'draft',
    version: 1
  }
];

const grades = [
  {
    id: '1',
    submission_id: '1',
    grade: 17.5,
    feedback: 'Excellent travail ! Le modèle atteint 94% de précision. Les visualisations sont claires et le code est bien structuré.',
    graded_by: '2',
    graded_at: '2025-11-16T10:00:00Z'
  }
];

const notifications = [
  {
    id: '1',
    user_id: '3',
    title: 'Nouvelle note disponible',
    message: 'Votre devoir "Projet IA - Classification d\'images" a été noté : 17.5/20',
    type: 'grade',
    is_read: false,
    created_at: '2025-11-16T10:00:00Z'
  },
  {
    id: '2',
    user_id: '3',
    title: 'Nouveau devoir assigné',
    message: 'Un nouveau devoir a été ajouté au cours "Développement Web Full-Stack"',
    type: 'assignment',
    is_read: true,
    created_at: '2025-10-15T09:00:00Z'
  }
];

const messages = [
  {
    id: '1',
    sender_id: '2',
    receiver_id: '3',
    subject: 'Question sur le projet IA',
    content: 'Bonjour Aminata, j\'ai regardé votre projet. Pourriez-vous m\'expliquer votre approche pour le preprocessing des images ?',
    is_read: false,
    created_at: '2025-11-12T11:30:00Z'
  },
  {
    id: '2',
    sender_id: '3',
    receiver_id: '2',
    subject: 'Re: Question sur le projet IA',
    content: 'Bonjour Professeure, j\'ai utilisé une normalisation des pixels et un data augmentation avec rotation et zoom pour améliorer la robustesse du modèle.',
    is_read: true,
    created_at: '2025-11-12T14:15:00Z'
  }
];

export const supabase = {
  from: (table: string) => ({
    select: (columns?: string) => ({
      eq: (column: string, value: any) => ({
        single: async () => {
          let data = null;
          let error = null;

          switch (table) {
            case 'users':
            case 'profiles':
              data = users.find(u => u[column as keyof typeof u] === value);
              if (!data) error = { message: 'User not found' };
              break;
            case 'departments':
              data = departments.find(d => d[column as keyof typeof d] === value);
              if (!data) error = { message: 'Department not found' };
              break;
            case 'subjects':
              data = subjects.find(s => s[column as keyof typeof s] === value);
              if (!data) error = { message: 'Subject not found' };
              break;
            case 'assignments':
              data = assignments.find(a => a[column as keyof typeof a] === value);
              if (!data) error = { message: 'Assignment not found' };
              break;
            case 'enrollments':
              data = enrollments.find(e => e[column as keyof typeof e] === value);
              if (!data) error = { message: 'Enrollment not found' };
              break;
            case 'submissions':
              data = submissions.find(s => s[column as keyof typeof s] === value);
              if (!data) error = { message: 'Submission not found' };
              break;
            case 'grades':
              data = grades.find(g => g[column as keyof typeof g] === value);
              if (!data) error = { message: 'Grade not found' };
              break;
            case 'notifications':
              data = notifications.find(n => n[column as keyof typeof n] === value);
              if (!data) error = { message: 'Notification not found' };
              break;
            case 'messages':
              data = messages.find(m => m[column as keyof typeof m] === value);
              if (!data) error = { message: 'Message not found' };
              break;
            default:
              error = { message: 'Table not found' };
          }

          return { data, error };
        },
        order: (column: string, options?: any) => ({
          limit: (count: number) => ({
            single: async () => {
              let data = null;
              let error = null;

              switch (table) {
                case 'subjects':
                  data = subjects.slice(0, count);
                  break;
                case 'assignments':
                  data = assignments.slice(0, count);
                  break;
                case 'notifications':
                  data = notifications.slice(0, count);
                  break;
                case 'messages':
                  data = messages.slice(0, count);
                  break;
                default:
                  data = [];
              }

              return { data, error };
            }
          })
        })
      }),
      single: async () => {
        let data = null;
        let error = null;

        switch (table) {
          case 'users':
          case 'profiles':
            data = users[0] || null;
            break;
          default:
            error = { message: 'Table not found' };
        }

        return { data, error };
      }
    }),
    insert: (data: any) => ({
      select: (columns?: string) => ({
        single: async () => {
          if (table === 'users' || table === 'profiles') {
            const newUser = { ...data, id: (users.length + 1).toString() };
            users.push(newUser);
            return { data: newUser, error: null };
          }
          return { data: null, error: { message: 'Table not found' } };
        }
      })
    }),
    update: (data: any) => ({
      eq: (column: string, value: any) => ({
        select: () => ({
          single: async () => {
            // Simulation d'update
            return { data: { ...data, [column]: value }, error: null };
          }
        })
      })
    })
  })
};

// Pour la compatibilité avec le code existant
export const pool = {
  execute: async (query: string, params: any[] = []) => {
    // Simulation de base de données pour les tests
    if (query.includes('SELECT') && query.includes('users') && query.includes('email = ?')) {
      const email = params[0];
      const user = users.find(u => u.email === email);
      return [user ? [user] : []];
    }
    return [[]];
  }
};

export default pool;