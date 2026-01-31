import { NextRequest, NextResponse } from 'next/server';
import { pool } from '@/lib/db';
import jwt from 'jsonwebtoken';

export const dynamic = 'force-dynamic';

export async function GET(request: NextRequest) {
  try {
    const token = request.headers.get('authorization')?.replace('Bearer ', '');

    if (!token) {
      return NextResponse.json({ error: 'Token manquant' }, { status: 401 });
    }

    const decoded = jwt.verify(token, process.env.JWT_SECRET!) as any;
    const userId = decoded.id;
    const userRole = decoded.role;

    let assignments: any[] = [];
    let submissions: any[] = [];

    if (userRole === 'student') {
      // Récupérer les assignments pour les cours où l'étudiant est inscrit
      const [assignmentRows] = await pool.execute(`
        SELECT a.*, s.title as subject_title, s.code as subject_code
        FROM assignments a
        JOIN subjects s ON a.subject_id = s.id
        JOIN enrollments e ON s.id = e.subject_id
        WHERE e.student_id = ?
        ORDER BY a.due_date ASC
      `, [userId]);

      // Récupérer les submissions de l'étudiant
      const [submissionRows] = await pool.execute(`
        SELECT sub.*, g.grade, g.feedback
        FROM submissions sub
        LEFT JOIN grades g ON sub.id = g.submission_id
        WHERE sub.student_id = ?
      `, [userId]);

      assignments = assignmentRows;
      submissions = submissionRows;
    } else if (userRole === 'teacher') {
      // Récupérer les assignments créés par l'enseignant
      const [assignmentRows] = await pool.execute(`
        SELECT a.*, s.title as subject_title, s.code as subject_code
        FROM assignments a
        JOIN subjects s ON a.subject_id = s.id
        WHERE a.teacher_id = ?
        ORDER BY a.due_date ASC
      `, [userId]);

      assignments = assignmentRows;
    } else if (userRole === 'admin') {
      // Récupérer tous les assignments
      const [assignmentRows] = await pool.execute(`
        SELECT a.*, s.title as subject_title, s.code as subject_code
        FROM assignments a
        JOIN subjects s ON a.subject_id = s.id
        ORDER BY a.due_date ASC
      `);

      assignments = assignmentRows;
    }

    return NextResponse.json({ assignments, submissions });
  } catch (error) {
    console.error('Erreur lors de la récupération des assignments:', error);
    return NextResponse.json({ error: 'Erreur serveur' }, { status: 500 });
  }
}